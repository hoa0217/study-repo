package designpattern.facadepattern;

import designpattern.facadepattern.movie.Amplifier;
import designpattern.facadepattern.movie.PopcornPopper;
import designpattern.facadepattern.movie.Projector;
import designpattern.facadepattern.movie.Screen;
import designpattern.facadepattern.movie.StreamingPlayer;
import designpattern.facadepattern.movie.TheaterLights;
import designpattern.facadepattern.movie.Tuner;

public class HomeTheaterFacade {

    private Amplifier amp;
    private Tuner tuner;

    private StreamingPlayer player;

    private Projector projector;

    private Screen screen;

    private TheaterLights lights;

    private PopcornPopper popper;

    public HomeTheaterFacade(Amplifier amp, Tuner tuner, StreamingPlayer player,
        Projector projector, Screen screen, TheaterLights lights, PopcornPopper popper) {
        this.amp = amp;
        this.tuner = tuner;
        this.player = player;
        this.projector = projector;
        this.screen = screen;
        this.lights = lights;
        this.popper = popper;
    }

    public void watchMovie(String movie) {
        System.out.println("영화 볼 준비 중");
        popper.on();
        popper.pop();
        lights.dim(10);
        screen.down();
        projector.on();
        projector.wideScreenMode();
        amp.on();
        amp.setStreamingPlayer(player);
        amp.setSurroundAudio();
        amp.setVolume(5);
        player.on();
        player.play(movie);
    }

    public void endMovie() {
        System.out.println("홈시어터를 끄는 중");
        popper.off();
        lights.on();
        screen.up();
        projector.off();
        amp.off();
        player.stop();
        player.off();
    }
}
