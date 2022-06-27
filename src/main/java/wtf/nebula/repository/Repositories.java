package wtf.nebula.repository;

import wtf.nebula.Nebula;

import java.util.HashMap;
import java.util.Map;

/**
 * A main for all repositories of this client
 *
 * @author aesthetical
 * @since 06/27/22
 */
public class Repositories {
    private static final Map<Class<? extends BaseRepository>, BaseRepository> repos = new HashMap<>();

    /**
     * Inits this repo and adds the repo
     * @param repo the repository
     */
    public static void add(BaseRepository repo) {
        repos.put(repo.getClass(), repo);
        Nebula.BUS.subscribe(repo);
        repo.init();
    }

    public static <T extends BaseRepository> T getRepo(Class<T> clazz) {
        return (T) repos.getOrDefault(clazz, null);
    }
}
