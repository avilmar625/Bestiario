import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bestiario.Avistamiento
import com.example.bestiario.AvistamientoDao
import com.example.bestiario.MonstruoUsuario
import com.example.bestiario.MonstruoUsuarioDao

@Database(entities = [Avistamiento::class, MonstruoUsuario::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    abstract fun avistamientoDao(): AvistamientoDao
    abstract fun monstruoUsuarioDao(): MonstruoUsuarioDao
    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                val nueva = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bestiario_db"
                ).fallbackToDestructiveMigration(true)
                    .build()
                instancia = nueva
                nueva
            }
        }
    }
}