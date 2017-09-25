package com.catherine.webservices.kotlin_sample;

import com.catherine.webservices.kotlin_sample.data.Person;
import com.catherine.webservices.kotlin_sample.data.Person2;
import com.catherine.webservices.toolkits.CLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catherine on 2017/7/31.
 */

public class JavaEntrance {
    private final static String TAG = "JavaEntrance";
    private Person diana;
    private Person2 lavender;

    public JavaEntrance() {
        diana = new Person(-1, "default", -1);
        lavender = new Person2(-1, "default", -1);
    }

    //不允许null
    public void printDiana() {
        diana.setAge(15);
        diana.setName("Diana");
        diana.setId(0);
        CLog.Companion.v(TAG, diana.toString());
    }

    //允许null
    public void printLavender() {
        lavender.age = 13;
        lavender.setName(null);
        lavender.setName("Lavender");
        lavender.setId(1);
        CLog.Companion.v(TAG, lavender.toString());
    }

    //泛型1
    public void printGenerics() {
        //Java泛型
        List composite = new ArrayList();
        composite.add("我是String");
        composite.add(false);
        composite.add(12);
        for (int i = 0; i < composite.size(); i++)
            CLog.Companion.v(TAG, composite.get(i).toString());

        //Kotlin泛型
        KotlinEntrance.INSTANCE.printGenerics();
    }

    //泛型2，这样的做法无法在Kotlin中实现，必须用Java实现
    public abstract class View<P extends Presenter> {
        P presenter;
    }

    //泛型2，这样的做法无法在Kotlin中实现，必须用Java实现
    public abstract class Presenter<V extends android.view.View> {
        V view;
    }

    //调用Kotlin
    public void callKotlin() {
        //调用Kotlin object
        KotlinEntrance.INSTANCE.printHello();

        //调用Kotlin class
        KotlinDynamicEntrance entrance = new KotlinDynamicEntrance();
        entrance.printParameters(10, "Byzantine");

        //调用多载方法，须在Kotlin中设定预设值
        entrance.printOptionalParameters(5, "Byzantine", 20, "Justinian");
        entrance.printOptionalParameters(99);

        //调用包下的Kotlin方法
        FunctionSetKt.printFunInPackage();

        //调用扩展方法
        CLog.Companion.v(TAG, FunctionSetKt.isEmpty("test") + "");
        CLog.Companion.v(TAG, FunctionSetKt.isEmpty("") + "");
    }

    private final static List<Runnable> runnables = new ArrayList<>();

    public void addRunnable(Runnable r) {
        runnables.add(r);
        CLog.Companion.v(TAG, "Added " + r + ", now you've got " + runnables.size() + " runnables");
    }

    public void removeRunnable(Runnable r) {
        runnables.remove(r);
        CLog.Companion.v(TAG, "Removed " + r + ", now you've got " + runnables.size() + " runnables");
    }
}
