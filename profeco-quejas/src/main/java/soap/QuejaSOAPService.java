/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package soap;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import java.util.Date;
/**
 *
 * @author Carlo
 */
@WebService
public class QuejaSOAPService {

    @WebMethod
    public String estadoQueja(String id) {
        return "PENDIENTE"; // MOCK – conectar a DB real si quieres
    }

    @WebMethod
    public String crearQuejaSOAP(String consumidor, String comercio, String descripcion) {
        System.out.println("Queja creada via SOAP");
        return "ID-"+new Date().getTime();
    }
}