package core.alloy.codegen;

import core.models.declare.DataConstraint;
import core.models.declare.data.NumericData;

import java.util.Map;

/**
 * Created by Vasiliy on 2017-10-23.
 */
public class DataConstraintGenerator {

    Map<String, NumericData> map;
    StringBuilder alloy;
    FunctionGenerator fnGen;

    public DataConstraintGenerator(int maxSameInstances, int bitwidth) {
        this.fnGen = new FunctionGenerator(maxSameInstances, bitwidth);
    }

    public String Generate(DataConstraint c, String name, Map<String, NumericData> map) {
        this.map = map;
        this.alloy = new StringBuilder();

        if (c.getName().equals("Init"))
            addInitDataConstraint(c, name);

        if (c.getName().equals("Absence")) {
            if (c.getArgs().size() > 1)
                addAbsenceDataConstraint(c, name, Integer.parseInt(c.taskB()));
            else
                addAbsenceDataConstraint(c, name);
        }

        if (c.getName().equals("Existence")) {
            if (c.getArgs().size() > 1)
                addExistenceDataConstraint(c, name, Integer.parseInt(c.taskB()));
            else
                addExistenceDataConstraint(c, name);
        }

        if (c.getName().equals("Exactly"))
            addExactlyDataConstraint(c, name, Integer.parseInt(c.taskB()));

        if (c.getName().equals("RespondedExistence"))
            addRespondedExistenceDataConstraint(c, name, name + "c");

        if (c.getName().equals("Response"))
            addResponseDataConstraint(c, name, name + "c");

        if (c.getName().equals("AlternateResponse"))
            addAlternateResponseDataConstraint(c, name, name + "c");

        if (c.getName().equals("ChainResponse"))
            addChainResponseDataConstraint(c, name, name + "c");

        if (c.getName().equals("Precedence"))
            addPrecedenceDataConstraint(c, name, name + "c");

        if (c.getName().equals("AlternatePrecedence"))
            addAlternatePrecedenceDataConstraint(c, name, name + "c");

        if (c.getName().equals("ChainPrecedence"))
            addChainPrecedenceDataConstraint(c, name, name + "c");

        if (c.getName().equals("NotRespondedExistence"))
            addNotRespondedExistenceDataConstraint(c, name, name + "c");

        if (c.getName().equals("NotResponse"))
            addNotResponseDataConstraint(c, name, name + "c");

        if (c.getName().equals("NotPrecedence"))
            addNotPrecedenceDataConstraint(c, name, name + "c");

        if (c.getName().equals("NotChainResponse"))
            addNotChainResponseDataConstraint(c, name, name + "c");

        if (c.getName().equals("NotChainPrecedence"))
            addNotChainPrecedenceDataConstraint(c, name, name + "c");

        if (c.getName().equals("Choice"))
            addChoiceDataConstraint(c, name, name + "a");

        if (c.getName().equals("ExclusiveChoice"))
            addExclusiveChoiceDataConstraint(c, name, name + "a");

        return alloy.toString();
    }

    private void addInitDataConstraint(DataConstraint one, String name) {
        alloy.append("fact { one te: TaskEvent | ").append(one.taskA()).append(" = te.task and te = TE0 and ").append(name).append("[te] }\n");
        alloy.append(fnGen.generateFunction(name, one.getFirstFunction(), map, one.getArgs()));
    }

    private void addExistenceDataConstraint(DataConstraint one, String fnName) {
        alloy.append("fact { some te: TaskEvent | te.task = ").append(one.taskA()).append(" and ").append(fnName).append("[te]}\n");
        alloy.append(fnGen.generateFunction(fnName, one.getFirstFunction(), map, one.getArgs()));
    }

    private void addExistenceDataConstraint(DataConstraint one, String fnName, int n) {
        alloy.append("fact { #{ te: TaskEvent | ").append(one.taskA()).append(" in te.task and ").append(fnName).append("[te]").append("} >= ").append(n).append(" }\n");
        alloy.append(fnGen.generateFunction(fnName, one.getFirstFunction(), map, one.getArgs()));
    }

    private void addAbsenceDataConstraint(DataConstraint one, String fnName) {
        alloy.append("fact { no te: TaskEvent | te.task = ").append(one.taskA()).append(" and ").append(fnName).append("[te] }\n");
        alloy.append(fnGen.generateFunction(fnName, one.getFirstFunction(), map, one.getArgs()));
    }

    private void addAbsenceDataConstraint(DataConstraint one, String fnName, int n) {
        alloy.append("fact { #{ te: TaskEvent | ").append(one.taskA()).append(" in te.task and ").append(fnName).append("[te]").append("} <= ").append(n).append(" }\n");
        alloy.append(fnGen.generateFunction(fnName, one.getFirstFunction(), map, one.getArgs()));
    }

    private void addExactlyDataConstraint(DataConstraint one, String fnName, int n) {
        alloy.append("fact { #{ te: TaskEvent | ").append(one.taskA()).append(" in te.task and ").append(fnName).append("[te]").append("} = ").append(n).append(" }\n");
        alloy.append(fnGen.generateFunction(fnName, one.getFirstFunction(), map, one.getArgs()));
    }

    private void addRespondedExistenceDataConstraint(DataConstraint one, String fFnName, String sFnName) {
        addActivation(one, fFnName);
        alloy.append("(some ote: TaskEvent | ").append(one.taskB()).append(" = ote.task and ").append(sFnName).append("[te, ote]").append(")").append(" }\n");
        alloy.append(fnGen.generateFunction(fFnName, one.getFirstFunction(), map, one.getArgs()));
        alloy.append(fnGen.generateFunction(sFnName, one.getSecondFunction(), map, one.getArgs()));
    }

    private void addResponseDataConstraint(DataConstraint one, String fFnName, String sFnName) {
        addActivation(one, fFnName);
        alloy.append("(some fte: TaskEvent | After[te, fte] and ").append(one.taskB()).append(" = fte.task and ").append(sFnName).append("[te, fte])}\n");
        alloy.append(fnGen.generateFunction(fFnName, one.getFirstFunction(), map, one.getArgs()));
        alloy.append(fnGen.generateFunction(sFnName, one.getSecondFunction(), map, one.getArgs()));
    }

    private void addAlternateResponseDataConstraint(DataConstraint one, String fFnName, String sFnName) {
        addActivation(one, fFnName);
        alloy.append("(some fte: TaskEvent | After[te, fte] and ").append(one.taskB()).append(" = fte.task and ").append(sFnName)
                .append("[te, fte] and (no ite: TaskEvent | After[te, ite] and After[ite, fte] and ").append(one.taskA())
                .append(" = ite.task and ").append(fFnName).append("[ite])) }\n");
        alloy.append(fnGen.generateFunction(fFnName, one.getFirstFunction(), map, one.getArgs()));
        alloy.append(fnGen.generateFunction(sFnName, one.getSecondFunction(), map, one.getArgs()));
    }

    private void addChainResponseDataConstraint(DataConstraint one, String fFnName, String sFnName) {
        addActivation(one, fFnName);
        alloy.append("(some fte: TaskEvent | Next[te, fte] and ").append(one.taskB()).append(" = fte.task and ").append(sFnName).append("[te, fte])}\n");
        alloy.append(fnGen.generateFunction(fFnName, one.getFirstFunction(), map, one.getArgs()));
        alloy.append(fnGen.generateFunction(sFnName, one.getSecondFunction(), map, one.getArgs()));
    }

    private void addPrecedenceDataConstraint(DataConstraint one, String fFnName, String sFnName) {
        addActivation(one, fFnName);
        alloy.append("(some fte: TaskEvent | After[fte, te] and ").append(one.taskB()).append(" = fte.task and ").append(sFnName).append("[te, fte])}\n");
        alloy.append(fnGen.generateFunction(fFnName, one.getFirstFunction(), map, one.getArgs()));
        alloy.append(fnGen.generateFunction(sFnName, one.getSecondFunction(), map, one.getArgs()));
    }

    private void addAlternatePrecedenceDataConstraint(DataConstraint one, String fFnName, String sFnName) {
        addActivation(one, fFnName);
        alloy.append("(some fte: TaskEvent | After[fte, te] and ").append(one.taskB()).append(" = fte.task and ").append(sFnName)
                .append("[te, fte] and (no ite: TaskEvent | After[fte, ite] and After[ite, te] and ").append(one.taskA())
                .append(" = ite.task and ").append(fFnName).append("[ite]))}\n");
        alloy.append(fnGen.generateFunction(fFnName, one.getFirstFunction(), map, one.getArgs()));
        alloy.append(fnGen.generateFunction(sFnName, one.getSecondFunction(), map, one.getArgs()));
    }

    private void addChainPrecedenceDataConstraint(DataConstraint one, String fFnName, String sFnName) {
        addActivation(one, fFnName);
        alloy.append("(some fte: TaskEvent | Next[fte, te] and ").append(one.taskB()).append(" = fte.task and ").append(sFnName).append("[te, fte])}\n");
        alloy.append(fnGen.generateFunction(fFnName, one.getFirstFunction(), map, one.getArgs()));
        alloy.append(fnGen.generateFunction(sFnName, one.getSecondFunction(), map, one.getArgs()));
    }

    private void addNotRespondedExistenceDataConstraint(DataConstraint one, String fFnName, String sFnName) {
        addActivation(one, fFnName);
        alloy.append("(no ote: TaskEvent | ").append(one.taskB()).append(" = ote.task and ").append(sFnName).append("[te, ote]").append(")").append(" }\n");
        alloy.append(fnGen.generateFunction(fFnName, one.getFirstFunction(), map, one.getArgs()));
        alloy.append(fnGen.generateNotFunction(sFnName, one.getSecondFunction(), map, one.getArgs()));
    }

    private void addNotResponseDataConstraint(DataConstraint one, String fFnName, String sFnName) {
        addActivation(one, fFnName);
        alloy.append("(no fte: TaskEvent | After[te, fte] and ").append(one.taskB()).append(" = fte.task and ").append(sFnName).append("[te, fte])}\n");
        alloy.append(fnGen.generateFunction(fFnName, one.getFirstFunction(), map, one.getArgs()));
        alloy.append(fnGen.generateNotFunction(sFnName, one.getSecondFunction(), map, one.getArgs()));
    }

    private void addNotChainResponseDataConstraint(DataConstraint one, String fFnName, String sFnName) {
        addActivation(one, fFnName);
        alloy.append("(no fte: TaskEvent | Next[te, fte] and ").append(one.taskB()).append(" = fte.task and ").append(sFnName).append("[te, fte])}\n");
        alloy.append(fnGen.generateFunction(fFnName, one.getFirstFunction(), map, one.getArgs()));
        alloy.append(fnGen.generateNotFunction(sFnName, one.getSecondFunction(), map, one.getArgs()));
    }

    private void addNotPrecedenceDataConstraint(DataConstraint one, String fFnName, String sFnName) {
        addActivation(one, fFnName);
        alloy.append("(no fte: TaskEvent | After[fte, te] and ").append(one.taskB()).append(" = fte.task and ").append(sFnName).append("[te, fte])}\n");
        alloy.append(fnGen.generateFunction(fFnName, one.getFirstFunction(), map, one.getArgs()));
        alloy.append(fnGen.generateNotFunction(sFnName, one.getSecondFunction(), map, one.getArgs()));
    }

    private void addNotChainPrecedenceDataConstraint(DataConstraint one, String fFnName, String sFnName) {
        addActivation(one, fFnName);
        alloy.append("(no fte: TaskEvent | Next[fte, te] and ").append(one.taskB()).append(" = fte.task and ").append(sFnName).append("[te, fte])}\n");
        alloy.append(fnGen.generateFunction(fFnName, one.getFirstFunction(), map, one.getArgs()));
        alloy.append(fnGen.generateNotFunction(sFnName, one.getSecondFunction(), map, one.getArgs()));
    }

    private void addChoiceDataConstraint(DataConstraint one, String fFnName, String sFnName) {
        alloy.append("fact { some te: TaskEvent | te.task = ").append(one.taskA()).append(" and ").append(fFnName)
                .append("[te] or te.task = ").append(one.taskB()).append(" and ").append(sFnName).append("[te, te]}\n");
        alloy.append(fnGen.generateFunction(fFnName, one.getFirstFunction(), map, one.getArgs()));
        alloy.append(fnGen.generateFunction(sFnName, one.getSecondFunction(), map, one.getArgs()));
    }

    private void addExclusiveChoiceDataConstraint(DataConstraint one, String fFnName, String sFnName) {
        alloy.append("fact { \nsome te: TaskEvent | te.task = ").append(one.taskA()).append(" and ").append(fFnName)
                .append("[te] or te.task = ").append(one.taskB()).append(" and ").append(sFnName).append("[te, te]\n")
                .append("(no te: TaskEvent | ").append(one.taskA()).append(" = te.task and ").append(fFnName)
                .append("[te]) or (no te: TaskEvent | ").append(one.taskB()).append(" = te.task and ")
                .append(sFnName).append("[te, te])\n").append("}\n");
        alloy.append(fnGen.generateFunction(fFnName, one.getFirstFunction(), map, one.getArgs()));
        alloy.append(fnGen.generateFunction(sFnName, one.getSecondFunction(), map, one.getArgs()));
    }

    private void addActivation(DataConstraint one, String fFnName) {
        alloy.append("fact { all te: TaskEvent | (").append(one.taskA()).append(" = te.task and ").append(fFnName).append("[te]) implies ");
    }
}
