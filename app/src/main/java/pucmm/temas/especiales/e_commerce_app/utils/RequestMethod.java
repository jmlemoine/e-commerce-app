package pucmm.temas.especiales.e_commerce_app.utils;

public enum RequestMethod {
    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");

    private String value;

    RequestMethod(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
