package de.til7701.fluxus.common.flumen;

import de.til7701.fluxus.ast.Ast;
import de.til7701.fluxus.common.checker.Checker;
import de.til7701.fluxus.common.environment.Environment;
import de.til7701.fluxus.common.type.klass.Klass;
import de.til7701.fluxus.common.util.Natives;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlumenCheckerTest {

    @Test
    void runChecker() {
        Environment env = new Environment(new Natives(System.in, System.out));
        Flumen flumen = new Flumen();
        for (Klass klass : flumen.getKlasses()) {
            env.getKlassRegister().registerKlass(klass);
        }

        List<Ast> asts = new Flumen().getAsts();
        List<Klass> klasses = new Flumen().getKlasses();
        for (int i = 0; i < asts.size(); i++) {
            Ast ast = asts.get(i);
            Klass klass = klasses.get(i);
            Checker checker = new Checker(env);
            checker.check(ast, klass.fullyQualifiedFluxusName());
            assertTrue(checker.getErrors().isEmpty(), checker.getErrors().toString());
        }
    }

}
