package simpledb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The Join operator implements the relational join operation.
 */
public class HashEquiJoin extends Operator {

  private static final long serialVersionUID = 1L;

  /**
   * @see HashEquiJoin@constructor
   */
  private final JoinPredicate p;
  /**
   * An iterator used for scanning the left table to be joined
   */
  private DbIterator child1;
  /**
   * An iterator used for scanning the right table to be joined
   */
  private DbIterator child2;
  /**
   * The TupleDesc of the joined table
   */
  private TupleDesc joinedTd;
  /**
   * A mapping which maps a field to the tuples in the left table whose field specified by the join
   * predicate is the key field.
   */
  private HashMap<Field, ArrayList<Tuple>> field2Tuples;
  /**
   * An iterator of the values of `field2Tuples`
   */
  private Iterator<ArrayList<Tuple>> field2TuplesIt;
  /**
   * The tuple corresponding to the current child2
   */
  private Tuple curChild2;

  /**
   * Constructor. Accepts to children to join and the predicate to join them on
   *
   * @param p      The predicate to use to join the children
   * @param child1 Iterator for the left(outer) relation to join
   * @param child2 Iterator for the right(inner) relation to join
   */
  public HashEquiJoin(JoinPredicate p, DbIterator child1, DbIterator child2) {
    this.p = p;
    this.child1 = child1;
    this.child2 = child2;
    this.joinedTd = TupleDesc.merge(child1.getTupleDesc(), child2.getTupleDesc());
  }

  public JoinPredicate getJoinPredicate() {
    return p;
  }

  public TupleDesc getTupleDesc() {
    return joinedTd;
  }

  public String getJoinField1Name() {
    return child1.getTupleDesc().getFieldName(p.getField1());
  }

  public String getJoinField2Name() {
    return child2.getTupleDesc().getFieldName(p.getField2());
  }

  public void open() throws DbException, NoSuchElementException, TransactionAbortedException {
    super.open();
    child1.open();
    child2.open();
    listIt = null;
    curChild2 = null;

    field2Tuples = new HashMap<>();
    while (child1.hasNext()) {
      Tuple t = child1.next();
      Field field = t.getField(p.getField1());
      ArrayList<Tuple> tuples = field2Tuples.computeIfAbsent(field, k -> new ArrayList<>());
      tuples.add(t);
    }
    field2TuplesIt = field2Tuples.values().iterator();
  }

  public void close() {
    super.close();
    child1.close();
    child2.close();
  }

  public void rewind() throws DbException, TransactionAbortedException {
    close();
    open();
  }

  transient Iterator<Tuple> listIt;

  private Tuple iterateArray() {
    assert curChild2 != null;

    if (listIt.hasNext()) {
      Tuple curChild1 = listIt.next();
      assert p.filter(curChild1, curChild2);
      // Join `curChild1` and `curChild2`.
      Tuple joinedTuple = new Tuple(joinedTd);
      int nChild1 = child1.getTupleDesc().numFields();
      int nChild2 = child2.getTupleDesc().numFields();
      for (int i = 0; i < nChild1; ++i) {
        joinedTuple.setField(i, curChild1.getField(i));
      }
      for (int i = 0; i < nChild2; ++i) {
        joinedTuple.setField(nChild1 + i, curChild2.getField(i));
      }
      return joinedTuple;
    } else {
      listIt = null;
      return null;
    }
  }

  /**
   * Returns the next tuple generated by the join, or null if there are no more tuples. Logically,
   * this is the next tuple in r1 cross r2 that satisfies the join predicate. There are many
   * possible implementations; the simplest is a nested loops join.
   * <p>
   * Note that the tuples returned from this particular implementation of Join are simply the
   * concatenation of joining tuples from the left and right relation. Therefore, there will be two
   * copies of the join attribute in the results. (Removing such duplicate columns can be done with
   * an additional projection operator if needed.)
   * <p>
   * For example, if one tuple is {1,2,3} and the other tuple is {1,5,6}, joined on equality of the
   * first column, then this returns {1,2,3,1,5,6}.
   *
   * @return The next matching tuple.
   * @see JoinPredicate#filter
   */
  protected Tuple fetchNext() throws TransactionAbortedException, DbException {
    if (listIt != null) {
      Tuple res = iterateArray();
      if (res != null) {
        return res;
      }
    }

    if (curChild2 == null) {
      if (!child2.hasNext()) {
        return null;
      } else {
        curChild2 = child2.next();
      }
    }

    while (true) {
      while (field2TuplesIt.hasNext()) {
        ArrayList<Tuple> list = field2TuplesIt.next();
        if (p.filter(list.iterator().next(), curChild2)) {
          listIt = list.iterator();
          return iterateArray();
        }
      }
      field2TuplesIt = field2Tuples.values().iterator();
      if (!child2.hasNext()) {
        return null;
      } else {
        curChild2 = child2.next();
      }
    }
  }

  @Override
  public DbIterator[] getChildren() {
    return new DbIterator[]{child1, child2};
  }

  @Override
  public void setChildren(DbIterator[] children) {
    assert children.length == 2;
    child1 = children[0];
    child2 = children[1];
    this.joinedTd = TupleDesc.merge(child1.getTupleDesc(), child2.getTupleDesc());
  }

}
