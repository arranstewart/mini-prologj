package prologj;

class Rule {
  public final Compound head;
  public final Compound body;
  
  /** body should usually be a conjunction of terms.
   * but, it might just be one. It could just be "true", even.
   * 
   * e.g.: 
   *  is_always_true :- true.
   * 
   * variables in rules:
   * - variables called "_" are all their own variable.
   * - variables with one name should all be the same variable.
   * 
   * _facs_ are rules with the body equal to TRUE.
   * 
   * @param head
   * @param body
   */
  public Rule(Compound head, Compound body) {
    this.head = head;
    this.body = body;
  }
  
  public String toString() {
    return "Rl(head: " + head.toString() + ", body: " + body.toString() + ")";
  }
}