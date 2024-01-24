package designpattern.facadepattern.movie;

public class TheaterLights {

    public void on() {
        System.out.println("조명이 켜졌습니다.");
    }

    public void off() {
        System.out.println("조명이 꺼졌습니다.");
    }

    public void dim(int dim) {
        System.out.println("조명 밝기를 "+dim+"%로 설정합니다.");
    }
}
