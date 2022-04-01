package io.github.purpleloop.game.witchfantasy.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;

import io.github.purpleloop.gameengine.action.model.dialog.IDialogController;

/** A dialog frame, coming on overlay over the game view. */
public class DialogFrame {

    /** Height of the frame. */
    private static final int FRAME_WIDTH = 800;

    /** Width of the frame. */
    private static final int FRAME_HEIGHT = 400;

    /** Left position of the overlay frame. */
    private static final int FRAME_OY = 50;

    /** Top position of the overlay frame. */
    private static final int FRAME_OX = 50;

    /** Margin for text. */
    private static final int MARGIN = 50;

    /** Top position of answers. */
    private static final int ANSWER_Y_OFFSET = 100;

    /** Vertical step between answers. */
    private static final int ANSWER_STEP_HEIGHT = 50;

    /** Size of the arrow used to choose answer. */
    private static final int ARROW_SIZE = 5;

    /** A dark gray transparent color. */
    private static final Color COLOR_DARK_GRAY_TRANSPARENT = new Color(50, 50, 50, 200);

    /** A stroke for frame outlines. */
    private static final Stroke FRAME_STROKE = new BasicStroke(2, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND);

    /** Font used for dialogs. */
    private Font dialogFont = null;

    /** @param dialogFont the font to use for dialogs. */
    public void setFont(Font dialogFont) {
        this.dialogFont = dialogFont;
    }

    /**
     * Draws a dialog frame.
     * 
     * @param g2 the graphics used for display
     * @param currentDialogStep the dialog to render
     */
    public void paintFrame(Graphics2D g2, IDialogController currentDialogStep) {

        // Frame background
        g2.setColor(COLOR_DARK_GRAY_TRANSPARENT);
        g2.fillRect(FRAME_OX, FRAME_OY, FRAME_WIDTH, FRAME_HEIGHT);

        // Frame border
        g2.setColor(Color.WHITE);
        g2.setStroke(FRAME_STROKE);
        g2.drawRect(FRAME_OX, FRAME_OY, FRAME_WIDTH, FRAME_HEIGHT);

        // Prompt
        g2.setFont(dialogFont);
        g2.drawString(currentDialogStep.getCurrentPrompt(), FRAME_OX + MARGIN, FRAME_OY + MARGIN);

        // Answers
        int answerY = FRAME_OY + ANSWER_Y_OFFSET;
        int currentChoiceIndex = currentDialogStep.getCurrentChoiceIndex();

        for (String answer : currentDialogStep.getPossibleAnswers()) {
            g2.drawString(answer, FRAME_OX + MARGIN, answerY);
            answerY += ANSWER_STEP_HEIGHT;
        }

        int currentChoiceOffset = FRAME_OY + ANSWER_Y_OFFSET
                + currentChoiceIndex * ANSWER_STEP_HEIGHT - 5;

        int arrowEndX = FRAME_OX + (int) (MARGIN * 0.7);
        int arrowEndY = currentChoiceOffset;

        g2.drawLine(FRAME_OX + (int) (MARGIN * 0.3), arrowEndY, arrowEndX, arrowEndY);
        g2.drawLine(arrowEndX - ARROW_SIZE, arrowEndY - ARROW_SIZE, arrowEndX, arrowEndY);
        g2.drawLine(arrowEndX - ARROW_SIZE, arrowEndY + ARROW_SIZE, arrowEndX, arrowEndY);

    }
}
