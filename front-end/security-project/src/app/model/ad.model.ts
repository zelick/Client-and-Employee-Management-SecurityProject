import { User } from "./user.model";

export interface Ad {
    id?: number,
    email: string,
    name: string,
    surname: string,
    activeFrom: Date,
    activeTo: Date,
    description: string,
    slogan: string
}