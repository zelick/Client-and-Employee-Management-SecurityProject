import { User } from "./user.model"

export interface AdRequest {
    id?: number,
    email: string,
    deadline: Date,
    activeFrom: Date,
    activeTo: Date,
    description: string
}