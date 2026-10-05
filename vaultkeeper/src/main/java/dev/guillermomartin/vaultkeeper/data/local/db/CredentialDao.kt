package dev.guillermomartin.vaultkeeper.data.local.db

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface CredentialDao {

    @Query("SELECT * FROM credentials ORDER BY site")
    fun observeAll(): Flow<List<CredentialEntity>>

    @Query("SELECT * FROM credentials ORDER BY site")
    suspend fun getAll(): List<CredentialEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<CredentialEntity>)

    /**
     * Respalda VaultContentProvider#query(): el SQL y los argumentos ligados los arma
     * el provider a partir de selection/selectionArgs/sortOrder recibidos del llamador
     * externo — Room solo ejecuta la query ya construida, no la valida.
     */
    @RawQuery
    fun rawQuery(query: SupportSQLiteQuery): Cursor
}
