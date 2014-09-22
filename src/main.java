public class main {

	public static void main(String[] args) {
		Player20 p = new Player20();
		p.setSeed(System.nanoTime());
		p.setEvaluation(new SphereEvaluation());
		p.run();
	}

}
