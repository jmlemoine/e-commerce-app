package pucmm.temas.especiales.e_commerce_app.utils;

public enum PhotoOptions {
    TAKE_PHOTO("Take Photo"), CHOOSE_GALLERY("Choose from Gallery"), CHOOSE_FOLDER("Choose from Folder"), CANCEL("Cancel");


    private String value;

    private PhotoOptions(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
