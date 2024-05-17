import { User } from "./user.model";

export interface Ad {
    email: string,
    name: string,
    surname: string,
    activeFrom: Date,
    activeTo: Date,
    description: string,
    slogan: string
}