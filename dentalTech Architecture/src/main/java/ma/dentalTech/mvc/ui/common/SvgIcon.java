package ma.dentalTech.mvc.ui.common;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class SvgIcon {

    public static ImageIcon load(String resourcePath, int width, int height) {
        try (InputStream is = SvgIcon.class.getResourceAsStream(resourcePath)) {
            if (is == null) throw new IllegalArgumentException("SVG introuvable: " + resourcePath);

            TranscoderInput input = new TranscoderInput(is);

            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            PNGTranscoder t = new PNGTranscoder();
            t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) width);
            t.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) height);

            // Batik écrit dans un buffer via Graphics2D
            TranscoderOutput output = new TranscoderOutput();
            t.transcode(input, output); // transcode

            // Alternative simple: TranscoderOutput ne donne pas directement l'image,
            // on fait une version "BufferedImageTranscoder" ci-dessous (mieux)
            // -> je te donne la meilleure version juste après.

            return new ImageIcon(img);
        } catch (Exception e) {
            throw new RuntimeException("Erreur chargement SVG: " + resourcePath, e);
        }
    }

    // ✅ Version correcte (BufferedImageTranscoder)
    public static ImageIcon loadBetter(String resourcePath, int width, int height) {
        try (InputStream is = SvgIcon.class.getResourceAsStream(resourcePath)) {
            if (is == null) throw new IllegalArgumentException("SVG introuvable: " + resourcePath);

            TranscoderInput input = new TranscoderInput(is);

            BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) width);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) height);
            transcoder.transcode(input, null);

            return new ImageIcon(transcoder.getImage());
        } catch (Exception e) {
            throw new RuntimeException("Erreur chargement SVG: " + resourcePath, e);
        }
    }

    // Transcoder qui produit une BufferedImage
    static class BufferedImageTranscoder extends PNGTranscoder {
        private BufferedImage image;

        @Override
        public BufferedImage createImage(int w, int h) {
            return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        public void writeImage(BufferedImage img, TranscoderOutput out) {
            this.image = img;
        }

        public BufferedImage getImage() {
            return image;
        }
    }
}
