package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;



public class Controller {
    public BorderPane borderPane;
    public ChoiceBox multBox;
    public GridPane gridpane;
    public Label content;
    public TextField heightText;
    public TextField widthText;
    private List<File> files;
    private List<String> convertedFiles = new ArrayList<>();
    //save the converted img in this path
    private String saveDirPath = "/Users/guoweijie/IDEA/FINAL PROJECT/src/save";

    public void upload(ActionEvent actionEvent) throws FileNotFoundException {
        // first, clear the entire scene
        gridpane.getChildren().clear();

        //choose one or more images from local computer
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files","*.jpg","*.png","*.jpeg","*.gif"));
        files = fileChooser.showOpenMultipleDialog(borderPane.getScene().getWindow());
        int rowIndex = 0;
        int colIndex = 0;
        //load each img in the fileList
        for(int i = 0; i < files.size(); i++){
            File file = files.get(i);

            // get the image obj
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);
            //get the text obj


            //set  width and height of the image
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);


            //set the position of img
            if(rowIndex == 3){
                rowIndex = 0;
                colIndex += 2;
            }
            // get the infomation of Img
            String info = "Name: " + file.getName() + "\n" + "Height: "+ image.getHeight() + "\n" + "Width" + image.getWidth() + "\n";
            Text text = new Text(info);

            // add the image obj and text obj into the GUI
            gridpane.add(text,colIndex+1,rowIndex);
            gridpane.add(imageView,colIndex,rowIndex++);


        }

    }

    public void save(ActionEvent actionEvent) throws IOException {
        Alert warn = new Alert(Alert.AlertType.WARNING);
        warn.setContentText("no img could be saved");
        if(convertedFiles.size() == 0){
            warn.show();
        }
        for(String source : convertedFiles){
            String targetPath = saveDirPath+ source.substring(source.lastIndexOf('/'));
            File sourceFile = new File(source);
            File targetFile = new File(targetPath);

            Files.copy(sourceFile.toPath(),targetFile.toPath());
        }



    }

    public void convert(ActionEvent actionEvent) throws InterruptedException, IOException, IM4JavaException {
        Alert warn = new Alert(Alert.AlertType.WARNING);
        warn.setContentText("need input width and height");

        // get the desired width and height from textFiled
        String w = widthText.getText();
        String h = heightText.getText();

        // warn if no input(width and height)
        if(w.equals("") && h.equals("")){
            warn.show();
        }

        int targetWidth =  Integer.parseInt(w);
        int targetHeight = Integer.parseInt(h);
        System.out.println(targetWidth + "and "+targetHeight);


        // if no img file is chose, error occurs
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("there is no file for converting process");
        if(files == null || files.size()  == 0){
            alert.show();
        }

        String format = multBox.getValue().toString();
        // convert the img one by one
        for (File file : files) {
            // run ImgJava to get the converted img
            String sourcePath = file.getPath();
            String desPath = sourcePath.substring(0, sourcePath.lastIndexOf('.') + 1) + format;
            ConvertCmd convertCmd = new ConvertCmd();
            IMOperation imo = new IMOperation();
            imo.addImage(sourcePath);
            // '!'  -> resize the img changing the aspect  ratio
            imo.resize(targetWidth,targetHeight,'!');
            imo.addImage(desPath);
            convertedFiles.add(desPath);
            convertCmd.run(imo);
        }






    }

}
