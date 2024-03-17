package datasource;

import java.io.IOException;
import java.util.Map;

public interface DataSource<DataType> {

    Map<String, DataType> getAll();
    boolean add(DataType data) throws IOException;
    DataType get(String key);
    boolean remove(String key) throws IOException;

}
