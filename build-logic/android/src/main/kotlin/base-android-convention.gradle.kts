import com.android.build.gradle.BaseExtension
import gradleext.gradleconventions.debugImplementation
import gradleext.gradleconventions.withVersionCatalog
import java.io.FileInputStream
import java.util.Properties

configure<BaseExtension> {
    signingConfigs {

        signingConfigs {
            getByName("debug") {
                storeFile = file("$rootDir/app/debug.keystore")
            }

            create("release") {
                /** 1) Заиметь keystore файл
                 *  2) Добавить значения пути к файлу и паролей
                 *     в системные переменные по ключам типа:
                 *     *Название проекта*_RELEASE_KEYSTORE_FILE_PATH
                 *  3) Изменить ключи в функции ниже
                 *  4) Обратиться к ближайшему Jenkins-гуру, чтобы это все
                 *     также заработало на CI/CD */
                val debugKeystoreFile = rootProject.file("mock_credentials.properties")
                val releaseKeystoreFile = rootProject.file("release_keystore.properties")

                val propsFile = if (releaseKeystoreFile.exists())
                    releaseKeystoreFile
                else
                    debugKeystoreFile

                val props = Properties()
                props.load(FileInputStream(propsFile))

                storeFile = file(props.getProperty("storeFile"))
                storePassword = props.getProperty("storePassword")
                keyAlias = props.getProperty("keyAlias")
                keyPassword = props.getProperty("keyPassword")
            }
        }

        setCompileSdkVersion(33)
        defaultConfig {
            minSdk = 21
            targetSdk = 33
            versionCode = 1
            versionName = "1.0"
            testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
        }

        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
                signingConfig = signingConfigs.getByName("release")
                proguardFiles(
                    getDefaultProguardFile("proguard-android.txt"),
                    "proguard-rules.pro"
                )
                buildConfigField("String", "BASE_URL", "\"http://136.243.136.206:3030\"")
                buildConfigField(
                    "String",
                    "BASE_GRAPHQL_URL",
                    "\"http://136.243.136.206:3030/graphql\""
                )
            }
            getByName("debug") {
                isMinifyEnabled = false
                buildConfigField("String", "BASE_URL", "\"http://136.243.136.206:3030\"")
                buildConfigField(
                    "String",
                    "BASE_GRAPHQL_URL",
                    "\"http://136.243.136.206:3030/graphql\""
                )
            }
        }
        compileOptions {
            targetCompatibility = JavaVersion.VERSION_11
            sourceCompatibility = JavaVersion.VERSION_11
        }
    }

    project.withVersionCatalog { libs ->
        dependencies {
            debugImplementation(libs.leakCanary)
        }
    }
}
