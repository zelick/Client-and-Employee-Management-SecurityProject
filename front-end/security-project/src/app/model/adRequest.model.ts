export interface AdRequest {
    id?: number,
    email: string,
    deadline: Date,
    activeFrom: Date,
    activeTo: Date,
    description: string
}