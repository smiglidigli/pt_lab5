package pt_lab5;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

public class Downloader3 implements Runnable { //do zadania 3

    private static String IMAGE_PATH = "C:\\Users\\admin\\Documents\\NetBeansProjects\\pt_lab5\\images\\";
    private static String IMAGE_URLS[] = {
        //"https://upload.wikimedia.org/wikipedia/commons/5/5d/01R_26_October_2010.jpg", // 54 MB
        //"https://upload.wikimedia.org/wikipedia/commons/0/05/01E_May_15_2013_1750Z.jpg", // 10 MB
        //"https://upload.wikimedia.org/wikipedia/commons/f/fe/01R_Oct_12_2012_0905Z.jpg", // 12 MB
        //"https://upload.wikimedia.org/wikipedia/commons/d/d2/01S_Dec_4_2011_0730Z.jpg", // 8 MB
        "https://otomotopl-imagestmp.akamaized.net/images_otomotopl/883154857_1_1080x720_30-tdi-v6-224-km-radom_rev001.jpg",
        "https://otomotopl-imagestmp.akamaized.net/images_otomotopl/883154857_2_1080x720_30-tdi-v6-224-km-dodaj-zdjecia_rev001.jpg",
        "https://otomotopl-imagestmp.akamaized.net/images_otomotopl/883154857_3_1080x720_30-tdi-v6-224-km-osobowe_rev001.jpg",
        "https://otomotopl-imagestmp.akamaized.net/images_otomotopl/883154857_4_1080x720_30-tdi-v6-224-km-osobowe_rev001.jpg"
    };

    private static BufferedImage resizeImage(BufferedImage image,
            int width, int height) {
        System.out.println("Resizing started");
        BufferedImage resizedImage = new BufferedImage(width, height,
                image.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        System.out.println("Resizing finished");
        return resizedImage;
    }

    private static BufferedImage processImage(BufferedImage image) {
        System.out.println("Processing started");
        float[] blurKernelData = new float[64];
        Arrays.fill(blurKernelData, 1.0f / 64.0f);
        Kernel kernel = new Kernel(8, 8, blurKernelData);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        BufferedImage processedImage = op.filter(image, null);
        System.out.println("Processing finished");
        return processedImage;
    }

    private static BufferedImage downloadImage(String url) {
        try {
            System.out.println("Download started");
            BufferedImage image = ImageIO.read(new URL(url));
            System.out.printf("Image downloaded, resolution %dx%d\n",
                    image.getWidth(), image.getHeight());
            return image;
        } catch (IOException e) {
            System.out.println("Image downloading failed");
            e.printStackTrace();
            return null;
        }
    }

    private static String saveImage(BufferedImage image, String path) {
        try {
            System.out.println("Saving original image started");
            File outputfile = new File(path);
            ImageIO.write(image, "jpg", outputfile);
            System.out.println("Image saved");
        } catch (IOException e) {
            System.out.println("Image saving failed");
            e.printStackTrace();
        }
        return path;
    }

    //<zadanie-3>
    //https://stackoverflow.com/questions/42360031/how-would-you-do-multiple-operations-on-a-java-8-stream
    //
    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> (new Thread(new Downloader3())).start()
        );

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        //jak wykonac po wszystkich watkach?
        long endTime = System.currentTimeMillis() - startTime;
        System.out.printf("Finished processing after %.4f seconds\n",
                endTime / 1000.0);

    }

    @Override
    public void run() {
        AtomicInteger i = new AtomicInteger();
        Stream<String> imageUrls = Stream.of(IMAGE_URLS).parallel();
        imageUrls.forEach(element -> {
            saveImage(resizeImage(processImage(downloadImage(element)), 480, 640), IMAGE_PATH + "image_" + i.getAndIncrement() + ".jpg");
        });
    }
    //</zadanie-3>
}
