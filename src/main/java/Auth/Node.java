package Auth;

public class Node {
	public String opcao;
	public Node esq = null;
	public Node dir = null;
	
	public Node(String val) {
		this.opcao = val;
	}
}