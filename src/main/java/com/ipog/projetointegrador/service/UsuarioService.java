package com.ipog.projetointegrador.service;
import com.ipog.projetointegrador.model.Usuario;
import com.ipog.projetointegrador.repository.UsuarioRepository;
import com.ipog.projetointegrador.util.PasswordProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Cria um usuario
    public ResponseEntity<String> createUsuario(Usuario usuario) {
        Optional<Usuario> searchUsuarioLogin = usuarioRepository.findByLogin(usuario.getLogin());
        if(searchUsuarioLogin.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ja existe um usuario com este login, por favor, utilize um login unico.");
        }

        Optional<Usuario> searchUsuarioEmail = usuarioRepository.findByEmail(usuario.getEmail());
        if(searchUsuarioEmail.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ja existe um usuario com este email, por favor, utilize um email unico.");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Usuario criado com sucesso");
    }

    // Busca todos usuarios
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    // Busca um usuario por ID
    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    // Busca um usuario pelo token
    public Optional<Usuario> getUsuarioByToken(String token) {
        return usuarioRepository.findByToken(token);
    }

    // Atualiza usuario
    public Usuario updateUsuario(Long id, Usuario usuarioDetails) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            Usuario existingUsuario = usuario.get();
            existingUsuario.setLogin(usuarioDetails.getLogin());
            existingUsuario.setSenha(usuarioDetails.getSenha());
            existingUsuario.setStatus(usuarioDetails.getStatus());
            existingUsuario.setEmail(usuarioDetails.getEmail());
            return usuarioRepository.save(existingUsuario);
        }
        return null;
    }

    // Deleta todos usuarios
    public void deleteAllUsuarios() {
        usuarioRepository.deleteAll();
    }

    // Deleta usuario
    public void deleteUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public void logoutUsuario(String token) {
        Optional<Usuario> usuario = usuarioRepository.findByToken(token);
        if (usuario.isPresent()) {
            Usuario existingUsuario = usuario.get();
            existingUsuario.cleanToken();
        }
    }

    public Usuario loginUsuario(String login, String senha) throws Exception {
        Optional<Usuario> usuario = usuarioRepository.findByLogin(login);
        if (usuario.isPresent()) {
            Usuario existingUsuario = usuario.get();
            boolean senhaCorreta = PasswordProvider.verifyPassphrase(senha, existingUsuario.getSenha());
            if(senhaCorreta) {
                existingUsuario.setToken();
                return existingUsuario;
            }
            throw new Exception("Credenciais incorretas");
        }
        throw new Exception("Credenciais incorretas");
    }
}
