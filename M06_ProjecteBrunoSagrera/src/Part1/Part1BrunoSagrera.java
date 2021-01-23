package Part1;

import java.io.IOException;

import java.util.Scanner;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Close;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.Open;
import org.basex.core.cmd.XQuery;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.iter.Iter;
import org.basex.query.value.item.Item;

public class Part1BrunoSagrera {
	static Context context = new Context();

	public static void main(String[] args) {
		obrirBD();
		System.out.println(
				"##########################################################################################################\n");
		System.out.println(
				"************************** PROGRAMA DE CONSULTA DEL FIXTER factbook.xml **********************************\n");
		System.out.println(
				"************************** Versió 1. BD incrustada en el programa **********************************\n");
		System.out.println(
				"##########################################################################################################\n");
		Consultarpaisos();
		try {
			consultardadespaisos();
		} catch (BaseXException e) {
			e.printStackTrace();
		}
	}

	public static void obrirBD() {
		// Intentem obrir la BD incrustada
		try {
			new Open("factbook").execute(context);
		} catch (Exception e) {
			// Si no es pot obrir la crea
			try {
				new CreateDB("factbook", "factbook.xml").execute(context);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void Consultarpaisos() {
		System.out.println(
				"************************** La llista actual de països és: **********************************\n");
		try {
			// Consulta 1 - Mostra un llistat amb tots els països
			String consultapaisos = "for $cou in //country " + " return $cou/name/data()";
			executarQuery(consultapaisos);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void consultardadespaisos() throws BaseXException {
		Scanner sc = new Scanner(System.in);
		String pais;
		do {
			System.out.println("\n***** Siusplau, introdueix un país vàlid: ");
			pais = sc.nextLine();
		} while ((processarQuery(
				"for $pais in //country " + " where $pais/name = '" + pais + "' return $pais/name/data()"))
						.length() == 0);
		System.out.println(
				"*********************************************************************************************");
		System.out.println(
				"*********************************************************************************************");
		try {
			// Consulta 2 - Consulta les dades d'inflació i religions d'un país determinat
			String inflacion = "for $pais in //country " + " where $pais/name = '" + pais
					+ "' return $pais/inflation/data()";
			String religiones = "for $pais in //country " + " where $pais/name = '" + pais
					+ "' return $pais/religions/data()";
			String gruposetnicos = "for $pais in //country " + " where $pais/name = '" + pais
					+ "' return $pais/ethnicgroups/data()";
			String sumakm = "for $pais in //country " + " where $pais/name = '" + pais
					+ "' return sum($pais/border/@length/data())";
			;

			System.out.println("**** Les dades del país escollit són: ");
			System.out.println("Nom: " + pais);
			if ((processarQuery(inflacion)).length() != 0) {
				System.out.println("***** L'inflació es d'un: ");
				System.out.print(processarQuery(inflacion));
				System.out.println("%");
			}
			System.out.println("***** Les religions oficials del país són: ");
			System.out.println(processarQuery(religiones));
			System.out.println("\n************************** GRUPS ÈTNICS  **********************************");
			System.out.println(processarQuery(gruposetnicos));
			System.out
					.println("************************** SUMA KM DE LES FRONTERES  **********************************");
			System.out.println(processarQuery(sumakm));
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// Tanquem la connexió i el context a la BD tant si s'ha executat amb èxit la
			// consulta com si no
			try {
				new Close().execute(context);
				context.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Mètode que executa una query fent servir la comanda execute() de l'objecte
	// XQuery
	private static String processarQuery(final String query) throws BaseXException {
		return (new XQuery(query).execute(context));
	}

	// Mètode que executa una query fent servir un processador de Querys (Query
	// Processor)
	private static void executarQuery(String query) throws QueryException {
		// Creem el Query Processor
		try (QueryProcessor proc = new QueryProcessor(query, context)) {
			// Guardem la direcció del resultat en un iterador
			Iter iter = proc.iter();

			// Iterem
			for (Item item; (item = iter.next()) != null;) {
				System.out.println(item.toJava());
			}
		}
	}

}
