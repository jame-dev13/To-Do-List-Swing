package jame.dev.Service;

import jame.dev.connection.ConnectionDB;
import jame.dev.models.Status;
import jame.dev.models.Task;
import jame.dev.repository.ITaskRepo;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log
public class TaskService implements ITaskRepo {

    @Override
    public int createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS tasks(
                id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
                uuid CHAR(36) NOT NULL,
                description VARCHAR(120) NOT NULL,
                priority SMALLINT NOT NULL,
                status VARCHAR(9) NOT NULL
                );
                """;
        Connection connection = null;
        try{
            connection = ConnectionDB.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute();
            ps.close();
            connection.commit();
            log.info("Table 'tasks' created.\n");
            return 0;
        }catch (SQLException e){
            log.info("Connection Error\n" + e.getMessage());
            try {
                connection.rollback();
            }catch (SQLException ex){
                log.info(ex.getMessage());
            }
        }finally {
            try{
                if(connection != null){
                    connection.setAutoCommit(true);
                    connection.close();
                    log.info("Connection closed! ");
                }
            }catch(SQLException e){
                log.info(e.getMessage());
            }
        }
        return 1;
    }

    @Override
    public void save(@NonNull Task task) {
        String sql = """
                INSERT INTO tasks (uuid, description, priority, status) 
                VALUES (?,?,?,?); 
                """;
        Connection con = null;
        try{
            con = ConnectionDB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, task.getUuid().toString());
            ps.setString(2, task.getDesc());
            ps.setInt(3, task.getPriority());
            ps.setString(4, task.getStatus().name());
            int rows = ps.executeUpdate();
            if(rows > 0) log.info(task + " added successfully");
            ps.close();
            con.commit();
        }catch(SQLException e){
            if(con != null){
                try{
                   con.rollback();
                }catch(SQLException ex){
                    log.info(ex.getMessage());
                }
            }
        }finally {
            try{
                if(con != null){
                    con.setAutoCommit(true);
                    con.close();
                }
            }catch(SQLException ex){
                log.info(ex.getMessage());
            }
        }
    }

    @Override
    public void update(@NonNull Task task) {
        String sql = """
                UPDATE tasks SET description = ?, priority = ?, status = ?
                 WHERE uuid = ?;
                """;
        Connection connection = null;
        try{
            connection = ConnectionDB.getInstance().getConnection();
            try(PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, task.getDesc());
                ps.setInt(2, task.getPriority());
                ps.setString(3, task.getStatus().name());
                ps.setString(4, task.getUuid().toString());
                int rows = ps.executeUpdate();
                log.info((rows > 0) ? "Data has been updated.\n " : "0 rows affected. \n");
            }

            connection.commit();
        }catch(Exception e){
            log.info("Connection Error\n" + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }finally {
            try{
                if (connection != null){
                    connection.setAutoCommit(true);
                    connection.close();
                }
            }catch(Exception e){
                log.info(e.getMessage());
            }
        }
    }

    @Override
    public Optional<Task> findTaskByUuid(@NonNull UUID uuid) {
        Optional<Task> task = Optional.empty();
        String sql = """
                SELECT * FROM tasks WHERE uuid = ?;
                """;
        try(Connection connection = ConnectionDB.getInstance().getConnection()){
            try(PreparedStatement ps = connection.prepareStatement(sql)){
                ps.setString(1, uuid.toString());
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()) {
                        task = Optional.of(
                                Task.builder()
                                        .id(rs.getInt(1))
                                        .uuid(UUID.fromString(rs.getString(2)))
                                        .desc(rs.getString(3))
                                        .priority(rs.getInt(4))
                                        .status(Status.valueOf(rs.getString(5)))
                                        .build());
                        System.out.println(task);
                    }
                }
            }
        }catch (SQLException e){
            log.info("Connection error\n" + e.getMessage());
            return Optional.empty();
        }
        return task;
    }

    @Override
    public void deleteTaskByUuid(@NonNull UUID uuid) {
        String sql = """
                DELETE FROM tasks WHERE uuid = ?;
                """;
        try(Connection connection = ConnectionDB.getInstance().getConnection()){
            try(PreparedStatement ps = connection.prepareStatement(sql)){
                ps.setString(1, uuid.toString());
                int rows = ps.executeUpdate();
                if(rows > 0)
                    log.info("Data from uuid: " +  uuid + " has been deleted");
                else
                    log.info("Cannot delete from uuid: " + uuid);
            }
            connection.commit();
        }catch(SQLException e){
            try (Connection connection = ConnectionDB.getInstance().getConnection()){
                if(connection != null){
                    connection.setAutoCommit(true);
                    connection.rollback();
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            log.info("Connection error: \n" + e.getMessage());
        }
    }

    @Override
    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        String sql = """
                SELECT * FROM tasks;
                """;
        try(Connection connection = ConnectionDB.getInstance().getConnection()){
            try(PreparedStatement ps = connection.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Task task = Task.builder()
                                .id(rs.getInt(1))
                                .uuid(UUID.fromString(rs.getString(2)))
                                .desc(rs.getString(3))
                                .priority(rs.getInt(4))
                                .status(Status.valueOf(rs.getString(5)))
                                .build();
                        tasks.add(task);
                    }
                }
            }
            return tasks;
        } catch (SQLException e) {
            log.info("Connection error\n" + e.getMessage());
            return List.of();
        }
    }

    @Override
    public void updateStatus(UUID uuid) {
        String sql = "Update tasks SET status = ? WHERE uuid = ?"; 
        try(Connection connection = ConnectionDB.getInstance().getConnection()){
            try(PreparedStatement st = connection.prepareStatement(sql)){
                st.setString(1, Status.COMPLETED.name());
                st.setString(2, uuid.toString());
                int rows = st.executeUpdate();
                if(rows > 0) 
                    log.info(() -> "Updated record from: " + uuid + " set to COMPLETED status."); 
                else 
                    log.info(() -> "0 rows affected.");
            }
            connection.commit();
        }catch(SQLException e){
            try(Connection connection = ConnectionDB.getInstance().getConnection()){
                if(connection != null){
                    connection.rollback();
                }
            }catch(SQLException ex){
                log.info(() -> "Connection error. \n" + e.getMessage());
            }
        }
    }
}
