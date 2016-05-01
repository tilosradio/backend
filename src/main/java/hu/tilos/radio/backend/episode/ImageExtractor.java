package hu.tilos.radio.backend.episode;

import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.RootNode;

public class ImageExtractor {

    private PegDownProcessor pegdown;

    public ImageExtractor() {
        this.pegdown = new PegDownProcessor(Extensions.HARDWRAPS | Extensions.AUTOLINKS);
    }

    public String extractFirstImage(String result) {
        RootNode rootNode = pegdown.parseMarkdown(result.toCharArray());
        ImageExtractorVisitor imageExtractorVisitor = new ImageExtractorVisitor();
        rootNode.accept(imageExtractorVisitor);
        return imageExtractorVisitor.getImage();
    }
}
