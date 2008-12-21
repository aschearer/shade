package com.shade.states;

import java.util.LinkedList;
import java.util.Scanner;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.ResourceLoader;

import com.shade.controls.Button;
import com.shade.controls.ClickListener;
import com.shade.controls.InstructionImage;
import com.shade.controls.InstructionText;
import com.shade.controls.SlickButton;
import com.shade.resource.ResourceManager;

public class InstructionState extends BasicGameState {

    private static final int INSTRUCTION_STATE_DELAY = 1000;
    private static final int INSTRUCTION_LENGTH = 6000;
    private static final int INSTRUCTION_BUFFER = 7500;

    public static final int ID = 7;

    private MasterState master;
    private ResourceManager resource;
    private SlickButton play, back;
    private int timer;

    private LinkedList<InstructionText> instructionText;
    private LinkedList<InstructionImage> instructionImages;

    private SpriteSheet instructionSheet;

    public InstructionState(MasterState m) throws SlickException {
        master = m;
        resource = m.resource;
        resource.register("backdrop", "states/instruction/backdrop.png");
        resource.register("instructions", "states/instruction/instructions.png");
        resource.register("skip-up", "states/instruction/skip-up.png");
        resource.register("skip-down", "states/instruction/skip-down.png");
        
        instructionSheet = new SpriteSheet(resource.get("instructions"), 90, 90);
    }

    @Override
    public int getID() {
        return ID;
    }

    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        throw new RuntimeException("CreditState was init'd!");
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game)
            throws SlickException {
        initButtons();
        initInstructions(master.jekyllSmall);
        timer = 0;
        if (!master.dimmer.finished()) {
            master.dimmer.reset();
        }
    }

    // render the aquarium
    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        master.control.render(game, g, resource.get("background"));
        master.dimmer.render(game, g);
        resource.get("header").draw(400, 0);
        play.render(game, g);
        back.render(game, g);
        resource.get("backdrop").draw(0, 400);
        for (InstructionText s : instructionText) {
            s.render(game, g);
        }
        for (InstructionImage s : instructionImages) {
            s.render(game, g);
        }
        resource.get("trim").draw();
    }

    // render the aquarium
    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        master.control.update(game, delta);
        master.dimmer.update(game, delta);
        timer += delta;
        if (timer > MasterState.STATE_TRANSITION_DELAY) {
            play.update(game, delta);
            back.update(game, delta);
        }
        for (InstructionImage s : instructionImages) {
            s.update(game, delta);
        }
        
        boolean finished = true;
        for (InstructionText s : instructionText) {
            s.update(game, delta);
            finished = finished & s.finished();
        }
        
        if (finished) {
            game.enterState(InGameState.ID, new FadeOutTransition(), null);
        }
        
    }

    private void initButtons() throws SlickException {
        initPlayButton();
        initBackButton();
    }

    private void initPlayButton() throws SlickException {
        play = new SlickButton(620, 110, resource.get("skip-up"), resource
                .get("skip-down"));
        play.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(InGameState.ID, new FadeOutTransition(), null);
            }

        });
    }

    private void initBackButton() throws SlickException {
        back = new SlickButton(620, 130, resource.get("back-up"), resource
                .get("back-down"));
        back.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(TitleState.ID);
                master.dimmer.reverse();
            }

        });
    }

    private void initInstructions(TrueTypeFont f) {
        instructionText = new LinkedList<InstructionText>();
        instructionImages = new LinkedList<InstructionImage>();
        Scanner s = new Scanner(ResourceLoader
                .getResourceAsStream("states/instruction/instructions.txt"));
        int x = 0; 
        int y = 0;
        int n = 0;
        while (s.hasNextLine()) {
            String[] credit = s.nextLine().split(":");
            InstructionImage i = new InstructionImage(50, 420, instructionSheet.getSprite(x, y));
            i.setTimer(INSTRUCTION_STATE_DELAY + n * INSTRUCTION_BUFFER);
            i.setDuration(INSTRUCTION_LENGTH);
            InstructionText t = new InstructionText(150, 455, credit[1], f);
            t.setTimer(INSTRUCTION_STATE_DELAY + n * INSTRUCTION_BUFFER);
            t.setDuration(INSTRUCTION_LENGTH);
            instructionImages.add(i);
            instructionText.add(t);
            n++;
            x++;
            if (x > 4) {
                x = 0;
                y++;
            }
        }
    }
    
}
