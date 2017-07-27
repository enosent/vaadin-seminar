package com.vseminar.image;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;

import java.io.*;
import java.util.regex.Pattern;

public class ImageUploader implements Upload.Receiver {

    private File file;
    private String imgPath;

    final static Pattern pattern = Pattern.compile("([^\\s]+(\\.(?i)(jpg|jpeg|gif|png))$)");

    public File getSuccessUploadFile() {
        return this.file;
    }

    public String getImgPath() {
        return this.imgPath;
    }

    public static File getFile(String imgPath) {
        String baseDirectory = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();

        return new File(baseDirectory + "/VAADIN/themes/" + UI.getCurrent().getTheme() + "/" + imgPath);
    }

    public static String getUrl(String imgPath) {
        String contextPath = VaadinServlet.getCurrent().getServletContext().getContextPath();
        return contextPath + "/VAADIN/themes/" + UI.getCurrent().getTheme() + "/" + imgPath;
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        FileOutputStream fos = null;

        try {

            boolean isAllowExt = pattern.matcher(filename).matches();

            if(!isAllowExt) {
                throw new IOException("allow extension *.jpg|jpeg|gif|png");
            }

            imgPath = "img/upload/" + filename;

            file = getFile(imgPath);

            if(file.exists()) {
                file.delete();
            }

            file.getParentFile().mkdirs();
            file.createNewFile();

            fos = new FileOutputStream(file);
        } catch (IOException e) {
            new Notification("Could not open file", e.getMessage(), Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
        }

        return fos;
    }

}
