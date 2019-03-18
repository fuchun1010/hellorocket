package com.tank.common;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author fuchun
 */
public class ConfigLoader {

  public interface PropsLoader<T> {

    /**
     * @param key
     * @param config
     * @return
     */
    T value(String key, Config config);
  }

  /**
   * @param key
   * @param propsLoader
   * @param <T>
   * @return
   */
  public static <T> T valueByKey(String key, PropsLoader<T> propsLoader) {

    File file = null;
    file = AppConfigUtil.loadConfigFile();
    Config config = ConfigFactory.parseFile(file);
    return propsLoader.value(key, config);
  }

  private ConfigLoader() {

  }

  static class AppConfigUtil {

    public static File loadConfigFile() {
      return Single.INSTANCE.loadConfig();
    }

    private enum Single {
      INSTANCE;

      Single() {
        try {
          this.file = this.loadDefaultFile();
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
      }

      public File loadConfig() {
        return this.file;
      }

      private File loadDefaultFile() throws FileNotFoundException {
        String dirHome = System.getProperty("user.dir");
        for (String module : modules) {
          dirHome = dirHome.replace(module, "");
        }
        String path = dirHome + File.separator + "config" + File.separator + "app.conf";
        File configFile = new File(path);
        if (!configFile.exists()) {
          throw new FileNotFoundException(path + " not exists");
        }
        return configFile;
      }

      File file = null;

      private String[] modules = new String[]{"common"};

    }

    private AppConfigUtil() {

    }

  }
}
