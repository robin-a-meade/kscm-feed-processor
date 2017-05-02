package edu.hawaii.kscmfeedprocessor;

/**
 *  Enum to hold data manipulation operation: INSERT or UPDATE or DELETE.
 */
public enum Op {

    INSERT("insert", "inserted"), UPDATE("update", "updated"), DELETE("delete", "deleted");

    private String presentTense;
    private String pastTense;

    Op(String presentTense, String pastTense) {
        this.presentTense = presentTense;
        this.pastTense = pastTense;
    }

    public String getPresentTense() {
        return presentTense;
    }

    public void setPresentTense(String presentTense) {
        this.presentTense = presentTense;
    }

    public String getPastTense() {
        return pastTense;
    }

    public void setPastTense(String pastTense) {
        this.pastTense = pastTense;
    }

    public static void main(String[] args)  {
        System.out.println(Op.INSERT.name());
        System.out.println(Op.INSERT.getPresentTense());
        System.out.println(Op.INSERT.getPastTense());
        System.out.println(Op.UPDATE.name());
        System.out.println(Op.UPDATE.getPresentTense());
        System.out.println(Op.UPDATE.getPastTense());
    }

}
