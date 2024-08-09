package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url="jdbc:mysql://localhost:3306/hospital";
    private static final String username="root";
    private static final String password="password123";

    public  static  void main(String [] args){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Scanner sc=new Scanner(System.in);
        try{
            Connection connection= DriverManager.getConnection(url,username,password);
            Patient patient=new Patient(connection,sc);
            Doctor doctor=new Doctor(connection);

            while(true){
                System.out.println("Hospital Management System ");
                System.out.println("1.Add Patient");
                System.out.println("2.view Patient");
                System.out.println("3.view Doctors");
                System.out.println("4.Book Patient");
                System.out.println("5.Exit");
                System.out.print("Enter your choice : ");
                int choice=sc.nextInt();
                switch (choice){
                    case 1:
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        bookAppointment(patient,doctor,connection,sc);
                        System.out.println();
                        break;
                    case 5:
                        System.out.println("Thank you for using Hospital Management System");
                        return;
                    default:
                        System.out.println("Invalid option");
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient,Doctor doctor,Connection connection,Scanner sc){
        System.out.println("Enter patient Id : ");
        int pId=sc.nextInt();
        System.out.println("Enter Doctor Id : ");
        int dId=sc.nextInt();
        System.out.println("Enter appointment date (YYYY-MM-DD): ");
        String date=sc.next();

        if(patient.getPatientById(pId) && doctor.getDoctorById(dId)){
            if(checkDoctorAvail(dId,date,connection)){
                String appQuery="insert into appointments(patient_id,doctor_id,appointment_date) values(?,?,?)";
                try{
                    PreparedStatement preparedStatement=connection.prepareStatement(appQuery);
                    preparedStatement.setInt(1,pId);
                    preparedStatement.setInt(2,dId);
                    preparedStatement.setString(3,date);
                    int rows= preparedStatement.executeUpdate();
                    if(rows>0){
                        System.out.println("Appointment Booked");
                    }else{
                        System.out.println("Failed to Book Appointment");
                    }
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }else{
                System.out.println("Doctor not available on this date");
            }
        }else{
            System.out.println("Either doctor or patient doesn't exit");
        }
    }

    public static boolean checkDoctorAvail(int dId,String date,Connection connection){
        String query="select count(*) from appointments where doctor_id=? and appointment_date=?";

        try{
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setInt(1,dId);
            preparedStatement.setString(2,date);
            ResultSet resultSet= preparedStatement.executeQuery();
            if(resultSet.next()){
                int count=resultSet.getInt(1);
                if(count==0){
                    return true;
                }
                else{
                    return false;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

}
