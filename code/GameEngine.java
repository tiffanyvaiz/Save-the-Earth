package gamedesign;

import gamedesign.Audio;
import gamedesign.Character;
import java.util.ArrayList;
import java.util.Iterator;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.text.Font;
import javafx.stage.Stage;

class ExtraCharacter extends Character{
	
	public ExtraCharacter(String fname, int n_frames, int[] ds) {
        super(fname, n_frames, ds);
    }

    protected void updateCurFrame(long elapsedTime) {
        long t = elapsedTime + lastRemainTime;
        while (t>durs[curFrame]) {
            t -= durs[curFrame];
            curFrame++;
            if (curFrame>=durs.length) {
                curFrame = -1;
                valid = false;
                break;
            }
        }
        lastRemainTime = t;
    }
}



public class GameEngine extends Application{
	
    Character ship; // the spaceship character, no ArrayList because of only one instance object
    Character bkg; // the background character, no ArrayList because of only one instance object
    AnchorPane pane; // the pane of the game window
    ArrayList<Character> aliens = new ArrayList<Character>(); // list of aliens
    ArrayList<Character> bullets = new ArrayList<Character>(); // list of fired bullets
    ArrayList<Character> flames = new ArrayList<Character>(); // list of explosive flames caused by destroyed aliens
    ArrayList<Character> hearts = new ArrayList<Character>();
    ArrayList<Character> weapons = new ArrayList<Character>();
    double accSpeed = 0.0; // acceleration factor of moving characters 
    final double BASE_SPEED = 0.50; // base speed of moving characters
    long lastTime, curTime; // the time stamps of the last state update and the current state update
    long elapsedTime; // the elapsed time between the last state update and the current state update
    int aliens_amount =10;
    int killed=0;
    int lives = 5;
    Label life_label;
    Button retry;
    Boolean game_over=false;
    
    @Override
    public void start(Stage primaryStage) {
        startGame(primaryStage);
    }
    
    void cleanup() {
        for (Iterator<Character> it=aliens.iterator(); it.hasNext();) {
            Character a = it.next();
            a.valid=false;
        }
        initVariable();
        initCharacters();
    }

    void startGame(Stage primaryStage) {
        // initialisation from start method goes here
        
        pane = new AnchorPane();
        pane.setOnKeyPressed(e->keyStrike(e));
        
        initCharacters();
        Scene scene = new Scene(pane, bkg.getWidth(), bkg.getHeight());
        retry = new Button();
        retry.setPrefSize(40, 40);
        retry.setStyle("-fx-font: 22 arial; -fx-base: #000000;");
        Image bki = new Image("sprites/retry0.png");
        retry.setGraphic(new ImageView(bki));
        retry.setLayoutX(0);
        retry.setLayoutY(bkg.getHeight()-280);
                    
         
        pane.getChildren().add(retry);
        retry.setOnAction(
             new EventHandler<ActionEvent>() {
                @Override
                  public void handle(ActionEvent e) {
                      restart(primaryStage);
                            
                  }
             }
        );
        
        
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
        
        
        
        pane.requestFocus();
        lastTime = System.currentTimeMillis();
        tm.start();
        
    }

    void restart(Stage stage) {
        cleanup();
        startGame(stage);
    }

    public static void main(String[] args)
    {
        
    	launch();
    }
    
    private void initVariable() {
        accSpeed = 0.0; // acceleration factor of moving characters 
        aliens_amount =10;
        killed=0;
        lives = 5;
        
        game_over=false;
    
    }
    
    
    
    private void initCharacters() {
        lastTime=System.currentTimeMillis();
    	// Background
        bkg = new Character("bg", 1, new int[]{10});
        bkg.setBnd(0, bkg.getFrame().getWidth(), 0, bkg.getFrame().getHeight());
        pane.getChildren().add(bkg.getView());        
        // Label
        life_label = new Label(" Lives:"+ String.valueOf(lives));
        life_label.setStyle("-fx-font: 30 arial; -fx-base: #000000;");
        //life_label.setLayoutX(0);
        //life_label.setLayoutY(bkg.getFrame().getHeight()-80);
        
        pane.getChildren().add(life_label);
        // Spaceship
        ship = new Character("spaceship", 1, new int[]{10});
        ship.setBnd(0, bkg.getFrame().getWidth(), 0, bkg.getFrame().getHeight());
        ship.setVx(BASE_SPEED);
        ship.setPos(bkg.getWidth()/2-ship.getWidth()/2, bkg.getHeight()/5*4.5-ship.getHeight()/2);
        pane.getChildren().add(ship.getView());
        // Aliens
        for (int n=0; n<aliens_amount; n++) {
            int[] durs = new int[15]; 
            for (int i=0;i<durs.length;i++) // randomize the duration of each frame
                durs[i] = 100;//50+(int)(50*Math.random()+1);
            Character s = new Character("SkeltonFrame", 15, durs);
            s.setBnd(0, bkg.getFrame().getWidth(), 0, bkg.getFrame().getHeight());
            s.setVx(BASE_SPEED*(Math.random()+1));
            s.setPos(n*80, Math.random()*bkg.getFrame().getHeight()/3);
                                             
            
            aliens.add(s);
            pane.getChildren().add(s.getView());
            
            
            
        }
        pane.setOnKeyTyped(e->keyStrike(e));
    }
    
    private void keyStrike(KeyEvent e) {
                
        
        accSpeed = BASE_SPEED/5;
        if (e.getCode() == KeyCode.LEFT && ship.getVx()<0) {
            ship.setVx(ship.getVx()-accSpeed);
        }
        else if (e.getCode()==KeyCode.RIGHT && ship.getVx()>0) {
            ship.setVx(ship.getVx()+accSpeed);
        }
        else if (e.getCode() == KeyCode.LEFT)
        {
            ship.setVx(-BASE_SPEED);
            
            Character weapon = new Character("weapon",1,new int[]{10});            
            weapon.posProperty().addListener((ev) ->checkAliensAttack(weapon));
            
            
            Character heart = new Character("life",1,new int[]{10});
            heart.posProperty().addListener((ev) ->checkGetLife(heart));
            lifePlus(heart);
            aliensAttack(weapon);
            
            
        }
          
        else if (e.getCode() == KeyCode.RIGHT)
        {
            ship.setVx(BASE_SPEED);
            
            Character weapon = new Character("weapon",1,new int[]{10});            
            weapon.posProperty().addListener((ev) ->checkAliensAttack(weapon));
                        
            Character heart = new Character("life",1,new int[]{10});
            heart.posProperty().addListener((ev) ->checkGetLife(heart));
            
            aliensAttack(weapon);
            lifePlus(heart);
            
        }
            
        if (e.getCode() == KeyCode.SPACE) {
            if (bullets.size() < 3) { // prevent the firing of the 4th bullet before any of the previous three vanishes 
                Character bullet = new Character("bullet", 1, new int[] { 10 }); // create a new bullet character
                bullet.posProperty().addListener((ev) ->checkBullet(bullet)); // add a handler to listen for the bullet's position changes
                bullet.setBnd(0, bkg.getFrame().getWidth(), 0, bkg.getFrame().getHeight());
                bullet.setVy(-BASE_SPEED * 8);
                double x = ship.getX();
                double y = ship.getY();
                bullet.setPos(x + ship.getWidth() / 2, y - bullet.getHeight());
                pane.getChildren().add(bullet.getView());
                bullets.add(bullet); 
                //add sound affect:laser
                Audio laser = new Audio("laser0.mp3");
                laser.getAudioClip().play();
            }
        }
    }

    private void checkBullet(Character b) {
        if (b.getY()<0) 
            b.valid = false;
        else if (aliens.size()>0) {
            for (Iterator<Character> it=aliens.iterator(); it.hasNext();) {
                Character a = it.next();
                if (a.collideWith(b)) {
                    double x = a.getX()+a.getWidth()/2;
                    double y = a.getY()+a.getHeight()/2;
                    a.valid = b.valid = false;
                    Character flame = new ExtraCharacter("ExplodeFrame", 9, new int[]{100, 100, 100, 100, 100, 100, 100, 100, 100}); 
                    flame.setBnd(0, bkg.getFrame().getWidth(), 0, bkg.getFrame().getHeight());
                    flame.setPos(x, y);
                    pane.getChildren().add(flame.getView());
                    flames.add(flame);       
                    killed++;
                    //add sound affect:explosion
                    Audio explosion = new Audio("explosion0.mp3");
                    explosion.getAudioClip().play();
                    if(killed==aliens_amount && game_over==false) 
                    {
                        //you win!!
                        Audio win_sound = new Audio("cheers0.mp3");
                        win_sound.getAudioClip().play();
                        Character win = new Character("win", 1, new int[] { 10 });
                        win.setBnd(0, bkg.getFrame().getWidth(), 0, bkg.getFrame().getHeight());
                        win.setPos(0, 0);

                        pane.getChildren().add(win.getView()); 
                        
                    }
                                        
                }   
                
            }
            
        }
    }
    private void checkAliensAttack(Character w)
    {
        if(w.getY()>bkg.getHeight())
           w.valid = false;
        else
        {
            if(ship.collideWith(w))
            {
                Audio explosion = new Audio("explosion0.mp3");
                explosion.getAudioClip().play();
                w.valid = false;
                lives--;
                
                if(lives<=0)
                {
                    //game over
                    Audio game_over_sound = new Audio("go0.mp3");
                    game_over_sound.getAudioClip().play();
                    
                    Character go = new Character("GameOver", 1, new int[] { 10 });
                    
                    go.setPos(0, 0);
                    go.setBnd(0, bkg.getWidth(), bkg.getHeight()/3, bkg.getHeight()/3*2);
                    
                    
                    pane.getChildren().add(go.getView()); 
                    //pane.getChildren().add(retry); 
                    
                    
                    lives=0;
                    game_over=true;  
                    
                
                             
                }
                life_label.setText(" Lives:"+ String.valueOf(lives));
            }
        }
    }
    private void checkGetLife(Character h)
    {
        if(h.getY()>bkg.getHeight())
           h.valid = false;
        else
        {
            if(ship.collideWith(h))
            {
                Audio explosion = new Audio("life0.mp3");
                explosion.getAudioClip().play();
                h.valid = false;
                lives++;
                
                life_label.setText(" Lives:"+ String.valueOf(lives));
            }
        }
    }
    
    private void aliensAttack(Character weapon)
    {
        /* aliens attack!  */
        for(int i=0;i<aliens.size();i++)
        {
            if(Math.random()*100+1>=20)
            {
                int size= aliens.size();
                double temp = size * Math.random();
                int index = (int)temp;
                
                weapon.setBnd(0, bkg.getFrame().getWidth(), 0, bkg.getFrame().getHeight());
                weapon.setVy(BASE_SPEED*0.8 );
                weapon.setPos(aliens.get(index).getX(),aliens.get(index).getY());
                Audio weapon_sound = new Audio("weapon0.mp3");
                weapon_sound.getAudioClip().play();
                
                weapons.add(weapon);
                pane.getChildren().add(weapon.getView());
            
            }
        }
            
    }
    
    private void lifePlus(Character heart)
    {
        
        for(int i=0;i<aliens.size()/2;i++)
        {
            if(Math.random()*100+1>=95)
            {
                int size= aliens.size();
                double temp = size * Math.random();
                int index = (int)temp;
                
                heart.setBnd(0, bkg.getFrame().getWidth(), 0, bkg.getFrame().getHeight());
                heart.setVy(BASE_SPEED*0.5 );
                heart.setPos(aliens.get(index).getX(),aliens.get(index).getY());
                Audio weapon_sound = new Audio("weapon0.mp3");
                weapon_sound.getAudioClip().play();
                
                hearts.add(heart);
                pane.getChildren().add(heart.getView());
            
            }
        }
            
    }
    
    
    
    
    AnimationTimer tm = new AnimationTimer(){
    	public void handle(long arg){
    		curTime = System.currentTimeMillis();
    		elapsedTime = curTime - lastTime;
    		
    		updateAll(elapsedTime);
    		reclaimCharacters();
    		//lastTime = curTime;
    		try{
    			Thread.sleep((long)(10));
    		}catch (InterruptedException e) {
                e.printStackTrace();
            }
    	}
    };
    
    
    private void reclaimCharacters() {
        for (Iterator<Character> it=bullets.iterator(); it.hasNext();) {
            Character b = it.next();
            if (!b.valid) {
                pane.getChildren().remove(b.iv);
                it.remove();
            }
        }
        for (Iterator<Character> it=aliens.iterator(); it.hasNext();) {
            Character a = it.next();
            if (!a.valid) {
                pane.getChildren().remove(a.iv);
                it.remove();
            }
        }
        for (Iterator<Character> it=flames.iterator(); it.hasNext();) {
            Character f = it.next();
            if (!f.valid) {
                pane.getChildren().remove(f.iv);
                it.remove();
            }
        }
        for (Iterator<Character> it=hearts.iterator(); it.hasNext();) {
            Character a = it.next();
            if (!a.valid) {
                pane.getChildren().remove(a.iv);
                it.remove();
            }
        }
        for (Iterator<Character> it=weapons.iterator(); it.hasNext();) {
            Character a = it.next();
            if (!a.valid) {
                pane.getChildren().remove(a.iv);
                it.remove();
            }
        }
    }
    
    public void updateAll(long elapsedTime) {
        double w = bkg.getFrame().getWidth();

        // update background
        bkg.update(elapsedTime);       
        // update spaceship
        ship.update(elapsedTime);
        // update aliens
        for (Character s: aliens) {
            s.update(elapsedTime);
            double x = s.getX();
            if (x>=(w-s.getFrame().getWidth()) || x<=0) // reverse moving direction when reaching at left and right boundaries
                s.setVx(-s.getVx());
        }        
        // update bullets
        for (Character s: bullets) {
            s.update(elapsedTime);
        }        
        // update explosive flames
        for (Character s: flames) {
            if (s.curFrame>=0) // playback if the flame has not been extinguished yet, curFrame=-1 if extinguished
                s.update(elapsedTime);
        }
        for (Character s: hearts) {
            s.update(elapsedTime);
        } 
        for (Character s: weapons) {
            s.update(elapsedTime);
        } 
    }
    
    
    
}
