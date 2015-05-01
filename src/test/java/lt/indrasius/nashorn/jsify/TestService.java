package lt.indrasius.nashorn.jsify;

/**
 * Created by mantas on 15.4.22.
 */
public class TestService {
    public String blockingMethod() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String name = int.class.getName();

        return "Hello";
    }

    public String blockingFailMethod() throws Exception {
        blockingMethod();

        throw new Exception("Error");
    }

    public String sayHello(HelloBean bean, int times) {
        return "Hello " + repeat(bean.getName() + " ", times) + "(id: " + bean.getId() + ")";
    }

    private String repeat(String str, int times) {
        return String.format(String.format("%%0%dd", times), 0).replace("0", str);
    }
}

class HelloBean {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        HelloBean bean = (HelloBean)obj;
        return bean.getId() == getId() && bean.getName().equals(getName());
    }
}


class HelloBeanWithArray extends HelloBean {
    private HelloBean[] children;

    public HelloBean[] getChildren() {
        return children;
    }

    public void setChildren(HelloBean[] children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}