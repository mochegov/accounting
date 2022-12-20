package mochegov.accounting.model;

public class ProcessEntryResult {
    private ProcessEntryResultType resultType;
    private String errorString;

    private ProcessEntryResult(ProcessEntryResultType resultType, String errorString) {
        this.resultType = resultType;
        this.errorString = errorString;
    }

    public static ProcessEntryResult resultOK() {
        return new ProcessEntryResult(ProcessEntryResultType.OK, "");
    }

    public static ProcessEntryResult resultError(String errorString) {
        return new ProcessEntryResult(ProcessEntryResultType.ERROR, errorString);
    }

    public String getErrorString() {
        return errorString;
    }

    public ProcessEntryResultType getResultType() {
        return resultType;
    }
}
