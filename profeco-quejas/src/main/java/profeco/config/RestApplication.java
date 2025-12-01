package profeco.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        // Registra recursos REST existentes
        classes.add(api.HolaResource.class);
        classes.add(test.TestResource.class);
        classes.add(auth.LoginResource.class);
        classes.add(api.AdminResource.class);
        classes.add(api.ConsumidorResource.class);
        classes.add(api.QuejaResource.class);
        
        // ✅ Filtro de seguridad
        classes.add(security.RoleFilter.class);
        
//        // ❌ COMENTA TEMPORALMENTE las clases JPA
//         classes.add(services.InicializadorService.class);
//         classes.add(services.UsuarioService.class);


        System.out.println("✅ RestApplication - Recursos registrados: " + classes.size());
        return classes;
    }
}
