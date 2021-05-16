package Lesson_1;

import java.util.ArrayList;

public class Box <T extends Fruit>{

    private ArrayList<T> list = new ArrayList<>();

    public ArrayList<T> getList() {
        return list;
    }

    public void setList(ArrayList<T> list) {
        this.list = list;
    }

    public void add(T someFruit) {
        list.add(someFruit);
    }

    public float getWeight() {
        if (list.isEmpty()) {
            return 0.0f;
        } else {
            return (list.get(0).getWeight() * list.size());
        }

    }

    public boolean compare(Box<? extends Fruit> box) {
        return (this.getWeight() == box.getWeight());
    }

    public void move(Box<T> box) {
        ArrayList<T> tempList = box.getList();
        tempList.addAll(this.list);
        box.setList(tempList);
        this.list.clear();
    }


    @Override
    public String toString() {
        return "Box{" +
                "list=" + list +
                '}';
    }
}
