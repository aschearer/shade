package com.shade.states;

import java.util.ArrayList;
import java.util.Scanner;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.ResourceLoader;

import com.shade.base.Animatable;
import com.shade.controls.Button;
import com.shade.controls.ClickListener;
import com.shade.controls.InstructionImage;
import com.shade.controls.InstructionText;
import com.shade.controls.KeyListener;
import com.shade.controls.SlickButton;
import com.shade.controls.TwoToneButton;
import com.shade.util.ResourceManager;

public class InstructionState extends BasicGameState {

    private static final int INSTRUCTION_STATE_DELAY = 1000;
    private static final int INSTRUCTION_BUFFER = 5000;

    public static final int ID = 7;

    private MasterState master;
    private ResourceManager resource;
    private SlickButton play, back;
    private TwoToneButton next, prev;
    private int timer;

    private InstructionSet instructions;

    private SpriteSheet instructionSheet;

    public InstructionState(MasterState m) throws SlickException {
        master = m;
        resource = m.resource;
        resource.register("backdrop", "states/instruction/backdrop.png");
        resource
                .register("instructions", "states/instruction/instructions.png");
        resource.register("skip-up", "states/instruction/skip-up.png");
        resource.register("skip-down", "states/instruction/skip-down.png");
        resource.register("next-up", "states/instruction/next-up.png");
        resource.register("next-down", "states/instruction/next-down.png");

        instructionSheet = new SpriteSheet(resource.get("instructions"), 90, 90);
        initInstructions(master.jekyllSmall);
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
        timer = 0;
        instructions.reset();
//        if (!master.dimmer.finished()) {
//            master.dimmer.reset();
//        }
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
        instructions.render(game, g);
        prev.render(game, g);
        next.render(game, g);
        renderInstructionStep();
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
            prev.update(game, delta);
            next.update(game, delta);
        }
        prev.active(instructions.started());
        if (instructions.finished()) {
            ((InGameState) game.getState(InGameState.ID)).newGame();
            game.enterState(InGameState.ID, new FadeOutTransition(), null);
            return;
        }
        instructions.update(game, delta);

    }

    private void renderInstructionStep() {
        master.jekyllXSmall.drawString(18, 495, instructions.current() + " of "
                + instructions.size());
    }

    private void initButtons() throws SlickException {
        initPlayButton();
        initBackButton();
        initFlowButtons();
    }

    private void initFlowButtons() throws SlickException {
        Image up = resource.get("next-up");
        Image down = resource.get("next-down");
        next = new TwoToneButton(760, 490, up, down);
        prev = new TwoToneButton(740, 490, up.getFlippedCopy(true, false), down
                .getFlippedCopy(true, false));

        next.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                instructions.next();
            }

        });
        
        next.register(Input.KEY_RIGHT, new KeyListener() {

            public void onPress(StateBasedGame game, int key) {
                instructions.next();
            }
            
        });

        prev.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                instructions.prev();
            }

        });
        
        prev.register(Input.KEY_LEFT, new KeyListener() {

            public void onPress(StateBasedGame game, int key) {
                instructions.prev();
            }
            
        });
    }

    private void initPlayButton() throws SlickException {
        play = new SlickButton(620, 110, resource.get("skip-up"), resource
                .get("skip-down"));
        play.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                ((InGameState) game.getState(InGameState.ID)).newGame();
                game.enterState(InGameState.ID, new FadeOutTransition(), null);
            }

        });
    }

    private void initBackButton() throws SlickException {
        back = new SlickButton(620, 130, resource.get("back-up"), resource
                .get("back-down"));
        back.addListener(new ClickListener() {

            public void onClick(StateBasedGame game, Button clicked) {
                game.enterState(SelectState.ID);
                master.dimmer.reverse();
            }

        });
    }

    private void initInstructions(TrueTypeFont f) {
        instructions = new InstructionSet();
        Scanner s = new Scanner(ResourceLoader
                .getResourceAsStream("states/instruction/instructions.txt"));
        int x = 0;
        int y = 0;
        int n = 0;
        while (s.hasNextLine()) {
            String[] credit = s.nextLine().split(":");
            InstructionImage i = new InstructionImage(70, 420, instructionSheet
                    .getSprite(x, y));
            i.setTimer(INSTRUCTION_STATE_DELAY + n * INSTRUCTION_BUFFER);
            InstructionText t = new InstructionText(170, 455, credit[1], f);
            t.setTimer(INSTRUCTION_STATE_DELAY + n * INSTRUCTION_BUFFER);
            instructions.add(i, t);
            n++;
            x++;
            if (x > 4) {
                x = 0;
                y++;
            }
        }
    }

    private class InstructionSet implements Animatable {

        private boolean finished;
        private int current;
        private ArrayList<InstructionImage> images;
        private ArrayList<InstructionText> text;

        public InstructionSet() {
            images = new ArrayList<InstructionImage>();
            text = new ArrayList<InstructionText>();
        }

        public void reset() {
            images.get(current).reset();
            text.get(current).reset();
            current = 0;
            images.get(current).activate();
            text.get(current).activate();
        }

        public int current() {
            return current + 1;
        }

        public int size() {
            return text.size();
        }

        public void add(InstructionImage i, InstructionText t) {
            if (text.size() == 0) {
                i.activate();
                t.activate();
            }
            images.add(i);
            text.add(t);
        }

        public void next() {
            images.get(current).deactivate();
            text.get(current).deactivate();
            if (current < text.size() - 1) {
                current++;
                images.get(current).activate();
                text.get(current).activate();
            } else {
                finished = true;
            }
        }

        public void prev() {
            images.get(current).deactivate();
            text.get(current).deactivate();
            current--;
            images.get(current).activate();
            text.get(current).activate();
        }

        public void render(StateBasedGame game, Graphics g) {
            for (InstructionImage s : images) {
                s.render(game, g);
            }

            for (InstructionText s : text) {
                s.render(game, g);
            }
        }

        public void update(StateBasedGame game, int delta) {
            for (InstructionImage s : images) {
                s.update(game, delta);
            }

            for (InstructionText s : text) {
                s.update(game, delta);
            }
        }

        public boolean started() {
            return current == 0;
        }

        public boolean finished() {
            return finished;
        }
    }

}
