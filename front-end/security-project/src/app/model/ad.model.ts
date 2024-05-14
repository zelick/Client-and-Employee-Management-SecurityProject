import { User } from "./user.model";

export interface Ad {
    id?: number,
    user: User,
    activeFrom: Date,
    activeTo: Date,
    description: string,
    slogan: string
}