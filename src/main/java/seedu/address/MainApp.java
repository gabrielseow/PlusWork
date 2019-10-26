package seedu.address;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.address.commons.core.Config;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.Version;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.commons.util.ConfigUtil;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ProjectDashboard;
import seedu.address.model.ReadOnlyProjectDashboard;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.ReadOnlyUserSettings;
import seedu.address.model.UserPrefs;
import seedu.address.model.UserSettings;
import seedu.address.model.util.SampleTaskDataUtil;
import seedu.address.storage.JsonProjectDashboardStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.JsonUserSettingsStorage;
import seedu.address.storage.ProjectDashboardStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.storage.UserPrefsStorage;
import seedu.address.storage.UserSettingsStorage;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(0, 6, 0, true);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Config config;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing +Work ]===========================");
        super.init();

        AppParameters appParameters = AppParameters.parse(getParameters());
        config = initConfig(appParameters.getConfigPath());

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        ProjectDashboardStorage projectDashboardStorage =
                new JsonProjectDashboardStorage(userPrefs.getProjectDashboardFilePath());
        UserSettingsStorage userSettingsStorage = new JsonUserSettingsStorage(userPrefs.getUserSettingsFilePath());
        storage = new StorageManager(projectDashboardStorage, userPrefsStorage, userSettingsStorage);

        initLogging(config);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s project dashboard, user settings
     * and {@code userPrefs}. <br>
     * The data from the sample project dashboard will be used instead if {@code storage}'s project dashboard
     * is not found, or a new project will be used instead if errors occur when reading {@code storage}'s
     * project dashboard.
     * Similarly, default user settings will be used instead if {@code storage}'s user settings is not found.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        Optional<ReadOnlyProjectDashboard> projectDashboardOptional;
        ReadOnlyProjectDashboard initialData;
        Optional<UserSettings> userSettingsOptional;
        ReadOnlyUserSettings userSettings = null;
        try {
            projectDashboardOptional = storage.readProjectDashBoard();
            if (!projectDashboardOptional.isPresent()) {
                logger.info("Data file not found. Will be starting with a sample ProjectDashboard");
            }
            initialData = projectDashboardOptional.orElseGet(SampleTaskDataUtil::getSampleProjectDashboard);
            userSettingsOptional = storage.readUserSettings();
            userSettings = userSettingsOptional.orElse(new UserSettings());
        } catch (DataConversionException e) {
            logger.warning("Data file not in the correct format. Will be starting with an empty Project");
            initialData = new ProjectDashboard();
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty Project");
            initialData = new ProjectDashboard();
        }

        return new ModelManager(initialData, userPrefs, userSettings);
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    /**
     * Returns a {@code Config} using the file at {@code configFilePath}. <br>
     * The default file path {@code Config#DEFAULT_CONFIG_FILE} will be used instead
     * if {@code configFilePath} is null.
     */
    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed;

        configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataConversionException e) {
            logger.warning("Config file at " + configFilePathUsed + " is not in the correct format. "
                    + "Using default config properties");
            initializedConfig = new Config();
        }

        //Update config file in case it was missing to begin with or there are new/unused fields
        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    /**
     * Returns a {@code UserSettings} using the file at {@code storage}'s user settings file path,
     * or a new {@code UserSettings} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserSettings initPrefs(UserSettingsStorage storage) {
        Path settingsFilePath = storage.getUserSettingsFilePath();
        logger.info("Using settings file : " + settingsFilePath);

        UserSettings initialisedSettings;
        try {
            Optional<UserSettings> settingsOptional = storage.readUserSettings();
            initialisedSettings = settingsOptional.orElse(new UserSettings());
        } catch (DataConversionException e) {
            logger.warning("UserSettings file at " + settingsFilePath + " is not in the correct format. "
                    + "Using default user settings");
            initialisedSettings = new UserSettings();
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty Project");
            initialisedSettings = new UserSettings();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserSettings(initialisedSettings);
        } catch (IOException e) {
            logger.warning("Failed to save settings file : " + StringUtil.getDetails(e));
        }

        return initialisedSettings;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using prefs file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataConversionException e) {
            logger.warning("UserPrefs file at " + prefsFilePath + " is not in the correct format. "
                    + "Using default user prefs");
            initializedPrefs = new UserPrefs();
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty ProjectDashboard");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting ProjectDashboard " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping +Work ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
