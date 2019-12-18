package pucmm.temas.especiales.e_commerce_app.entities;

public class ErrorMessage {
    private boolean success;
    private boolean error;
    private String message;

    public ErrorMessage() { }

    public ErrorMessage(boolean success, boolean error, String message) {
        this.success = success;
        this.error = error;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
