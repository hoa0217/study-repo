package designpattern.facadepattern;

import designpattern.facadepattern.movie.Amplifier;
import designpattern.facadepattern.movie.PopcornPopper;
import designpattern.facadepattern.movie.Projector;
import designpattern.facadepattern.movie.Screen;
import designpattern.facadepattern.movie.StreamingPlayer;
import designpattern.facadepattern.movie.TheaterLights;
import designpattern.facadepattern.movie.Tuner;

public class HomeTheaterTestDrive {

    public static void main(String[] args) {
        Amplifier amp = new Amplifier();
        Tuner tuner = new Tuner();
        StreamingPlayer player = new StreamingPlayer();
        Projector projector = new Projector();
        Screen screen = new Screen();
        TheaterLights lights = new TheaterLights();
        PopcornPopper popper = new PopcornPopper();

        HomeTheaterFacade homeTheater = new HomeTheaterFacade(amp, tuner, player, projector,
            screen, lights, popper);

        homeTheater.watchMovie("인디아나 존스:레이터스");
        homeTheater.endMovie();
    }
}
