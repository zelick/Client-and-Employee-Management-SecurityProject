import { ClientType } from "./clientType.model";
import { RegistrationStatus } from "./registrationStatus.model";
import { ServicesPackage } from "./servicesPackage.model";
import { UserRole } from "./userRole.model";

export interface User{
    id?: number,
    email: string, 
    password: string, 
    name: string,
    surname: string,
    address: string, 
    city: string, 
    country: string, 
    phoneNumber: string,
    role: UserRole, 
    clientType: ClientType, 
    servicesPackage: ServicesPackage,
    registrationStatus: RegistrationStatus
}