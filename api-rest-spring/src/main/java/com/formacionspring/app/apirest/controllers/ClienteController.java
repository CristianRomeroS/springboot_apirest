package com.formacionspring.app.apirest.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.formacionspring.app.apirest.entity.Cliente;
import com.formacionspring.app.apirest.service.ClienteService;

@RestController
@RequestMapping("/api")
public class ClienteController {
	
	@Autowired
	private ClienteService servicio;
	
	@GetMapping("/clientes")
	public List<Cliente> clientes(){
		return servicio.findAll();
	}
	/*
	@GetMapping("/clientes/{id}")
	public Cliente clientById(@PathVariable long id){
		return servicio.findById(id);
	}
	*/
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> clienteShow(@PathVariable Long id){
		Cliente cliente=null;
		Map<String,Object> response=new HashMap<>();
		
		try {
			cliente=servicio.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar consulta base de datos");
			response.put("error", e.getMessage().concat("_ ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(cliente==null) {
			response.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);

		}
		
		return new ResponseEntity<Cliente>(cliente,HttpStatus.OK);
	}
	
	
//	@PostMapping("/clientes")
//	@ResponseStatus(HttpStatus.CREATED)
//	public Cliente save(@RequestBody Cliente cliente) {
//		return servicio.save(cliente);
//		
//	}
	
	@PostMapping("/clientes")
	public ResponseEntity<?> save(@RequestBody Cliente cliente) {
		Cliente clienteNew=null;
		Map<String,Object> response=new HashMap<>();
		try {
			clienteNew=servicio.save(cliente);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar insercion en la base de datos");
			response.put("error", e.getMessage().concat("_ ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El cliente ha sido creado con exito");
		response.put("cliente", clienteNew);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
		
	}
	
	
//	@PutMapping("/clientes/{id}")
//	@ResponseStatus(HttpStatus.CREATED)
//	public Cliente updateCliente( @RequestBody Cliente cliente, @PathVariable long id) {
//		Cliente clienteUpdate=servicio.findById(id);
//		
//		clienteUpdate.setNombre(cliente.getNombre());
//		clienteUpdate.setApellido(cliente.getApellido());
//		clienteUpdate.setEmail(cliente.getEmail());
//		clienteUpdate.setTelefono(cliente.getTelefono());
//		clienteUpdate.setCreatedAt(cliente.getCreatedAt());	
//		return servicio.save(clienteUpdate);
//		
//	}
	
	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> updateCliente( @RequestBody Cliente cliente, @PathVariable long id) {
		Cliente clienteActual=servicio.findById(id);
		Map<String,Object> response=new HashMap<>();
		try {
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setTelefono(cliente.getTelefono());
			clienteActual.setCreatedAt(cliente.getCreatedAt());		
			servicio.save(clienteActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar modificacion en el cliente ");
			response.put("error", e.getMessage().concat("_ ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El cliente ha sido modificado  con exito");
		response.put("cliente", clienteActual);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
//	@DeleteMapping("/clientes/{id}")
//	@ResponseStatus(HttpStatus.OK)
//	public void deleteCleinte(@PathVariable Long id) {
//		servicio.delete(id);
//	}
	
	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> deleteCleinte(@PathVariable Long id) {
		Cliente clienteBorrado=null;
		
		Map<String,Object> response=new HashMap<>();
				
			clienteBorrado=servicio.findById(id);
			if(clienteBorrado==null) {
				response.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe en la base de datos")));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);

			}else {
				try {
					servicio.delete(id);
					String nombreFotoAnterior = clienteBorrado.getImagen();
					
					if(nombreFotoAnterior !=null && nombreFotoAnterior.length()>0 ) {
						
						Path rutaFotoAnterior= Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
						File archivoFotoanterior = rutaFotoAnterior.toFile();
						
						if(archivoFotoanterior.exists() && archivoFotoanterior.canRead() ) {
							
							archivoFotoanterior.delete();
						}
					}
				} catch (DataAccessException e) {
					response.put("mensaje", "Error al borrar el cliente ");
					response.put("error", e.getMessage().concat("_ ").concat(e.getMostSpecificCause().getMessage()));
					return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}

		response.put("mensaje", "El cliente ha sido borrado con exito");
		response.put("cliente", clienteBorrado);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	@PostMapping("/clientes/upload")
	public ResponseEntity<?> uploadImagen(@RequestParam("archivo")MultipartFile archivo, @RequestParam("id")Long id){
		Map<String,Object> response=new HashMap<>();
		Cliente cliente=servicio.findById(id);
		if(!archivo.isEmpty()) {
			String nombreArchivo= UUID.randomUUID().toString()+"_"+archivo.getOriginalFilename().replace(" ","");
			Path rutaArchivo=Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
			try {
				Files.copy(archivo.getInputStream(), rutaArchivo);
			} catch (IOException e) {
				response.put("mensaje", "Error al intentar subir la imagen ");
				response.put("error", e.getMessage().concat("_ ").concat(e.getCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			String nombreFotoAnterior = cliente.getImagen();
			
			if(nombreFotoAnterior !=null && nombreFotoAnterior.length()>0 ) {
				
				Path rutaFotoAnterior= Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
				File archivoFotoanterior = rutaFotoAnterior.toFile();
				
				if(archivoFotoanterior.exists() && archivoFotoanterior.canRead() ) {
					
					archivoFotoanterior.delete();
				
				}
			}

			cliente.setImagen(nombreArchivo);
			servicio.save(cliente);
			response.put("mensaje", "Se ha subido correctamente el archivo: " + nombreArchivo);
			response.put("cliente", cliente);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		}else {
			response.put("Archivo", "Archivo vacio");
		}
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
//	@DeleteMapping("/cliente/{id}")
//	@ResponseStatus(HttpStatus.OK)
//	public Cliente deletecliente(@PathVariable long id) {
//		Cliente clienteUpdate=servicio.findById(id);
//		servicio.delete(id);
//		return clienteUpdate;
//		
//	}
}
