public class ORMainClass {
    public static void main(String[] args) {
        SimplexModel newModel = new SimplexModel() ;
        newModel.inputModelData() ;
        TableModel test = new TableModel ( newModel ) ;
        test.createTable() ;
        test.solve() ;
    }
}