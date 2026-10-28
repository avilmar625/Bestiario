import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bestiario.Avistamiento
import com.example.bestiario.AvistamientoDao

@Database(entities = [Avistamiento::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun avistamientoDao(): AvistamientoDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                val nueva = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bestiario_db"
                ).build()
                instancia = nueva
                nueva
            }
        }
    }
}