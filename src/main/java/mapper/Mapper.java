package mapper;

public interface Mapper <T, F>{
    T mapFrom(F entity);
}
