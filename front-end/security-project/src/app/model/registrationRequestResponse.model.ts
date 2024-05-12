import { User } from "./user.model";

export interface RegistrationRequestResponse {
    email: String,
    isAccepted: boolean,
    reason: string
}