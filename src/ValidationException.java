public class ValidationException extends Exception {
    private String msg;
    public ValidationException() {
        msg = "Modifica non applicabile al contratto\n";
    }

    public ValidationException(String s) {
        super(s);
    }


}
