package br.com.fiap.domain.repository;

import br.com.fiap.domain.entity.Equipamento;
import br.com.fiap.infra.ConnectionFactory;

import java.net.ConnectException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

public class EquipamentoRepository implements Repository<Equipamento, Long> {

    private static final AtomicReference<EquipamentoRepository> instance = new AtomicReference<>();

    private EquipamentoRepository() {
    }


    public static EquipamentoRepository build() {
        EquipamentoRepository result = instance.get();
        if (Objects.isNull(result)) {
            EquipamentoRepository repo = new EquipamentoRepository();
            if (instance.compareAndSet(null, repo)) {
                result = repo;
            } else {
                result = instance.get();
            }
        }
        return result;
    }


    @Override
    public Equipamento persist(Equipamento equipamento) {
        var sql = "BEGIN" +
                "INSERT INTO cliente (NM_EQUIPAMENTO) " +
                "VALUES (?) " +
                "returning ID_EQUIPAMENTO into ?; " +
                "END;" +
                "";

        var factory = ConnectionFactory.build();
        Connection connection = factory.getConnection();


        CallableStatement cs = null;
        try {
            cs = connection.prepareCall(sql);
            cs.setString(1, equipamento.getNome());
            cs.registerOutParameter(2, Types.BIGINT);
            cs.executeUpdate();
            equipamento.setId(cs.getLong(2));
            cs.close();
            connection.close();

        } catch (SQLException e) {
            System.err.println("Não foi possível executar o comando!\n" + e.getMessage());
        }
        return equipamento;
    }

    @Override
    public List<Equipamento> findAll() {
        List<Equipamento> equipamentos = new ArrayList<>();

        try {
            var factory = ConnectionFactory.build();
            Connection connection = factory.getConnection();

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM EQUIPAMENTO");

            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("ID_EQUIPAMENTO");
                    String nome = resultSet.getString("NM_EQUIPAMENTO");
                    String descricao = resultSet.getString(" DS_EQUIPAMENTO");
                    //Adicionando Equipamentos na coleção
                    equipamentos.add(new Equipamento(id, nome, descricao));
                }
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println("Não foi possivel consultar os dados!\n" + e.getMessage());
        }
        return equipamentos;
    }

    @Override
    public Equipamento findById(Long id) {
        Equipamento equipamento = null;
        var sql = "SELECT * FROM cliente where ID_EQUIPAMENTO=?";

        var factory = ConnectionFactory.build();
        Connection connection = factory.getConnection();

        try {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setLong(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.isBeforeFirst()) {
                    while (resultSet.next()) {
                        equipamento = new Equipamento(
                                resultSet.getLong("ID_EQUIPAMENTO"),
                                resultSet.getString("NM_EQUIPAMENTO"),
                                resultSet.getString(" DS_EQUIPAMENTO")
                        );
                    }
                } else {
                    System.out.println("Cliente não encontrado com o id = " + id);
                }
                resultSet.close();
                preparedStatement.close();
                connection.close();
            }catch (SQLException e) {
            System.err.println( "Não foi possível executar a consulta: \n" + e.getMessage() );
        }
        return equipamento;
    }

        @Override
        public Equipamento update (Equipamento equipamento){
            PreparedStatement ps = null;

            var sql = "UPDATE equipamento SET NM_EQUIPAMEnTO = ? where ID_EQUIPAMENTO";
            var factory = ConnectionFactory.build();
            Connection connection = factory.getConnection();
            try {
                ps = connection.prepareStatement(sql);
                ps.setString(1, equipamento.getNome());
                ps.setLong(2,equipamento.getId());
                int intensAtualizados = ps.executeUpdate();

                ps.close();
                connection.close();
                if (intensAtualizados > 0) return  findById(equipamento.getId());
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        public boolean delete (Long id){
            PreparedStatement ps = null;

            var sql = "DELETE from equipamento where ID_EQUIPAMENTO =?";
            var factory = ConnectionFactory.build();
            Connection connection = factory.getConnection();
            try {
                ps = connection.prepareStatement(sql);
                ps.setLong(1, id);
                int intensRemovidos = ps.executeUpdate();
                ps.close();
                connection.close();
                if (intensRemovidos > 0) return true;
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
}

