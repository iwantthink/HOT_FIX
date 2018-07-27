package luck.ryan;

/**
 * Created by renbo on 2018/3/8.
 */

public class HelloJava implements ISayHello {

    Test mTest = new Test();

    @Override
    public String say() {
        mTest.test();
        return "i am hello  from original apk version 222";
    }
}
