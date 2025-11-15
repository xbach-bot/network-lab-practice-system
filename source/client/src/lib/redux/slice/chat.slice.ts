import { IUser } from "@/types/backend";
import { createSlice } from "@reduxjs/toolkit";


interface IState{
    chatVisible: boolean;
    chatTarget: IUser | null;
}

const initialState: IState = {
    chatVisible: false,
    chatTarget: null,
};

export const chatSlice = createSlice({
    name: "chat",
    initialState,
    reducers: {
        setChatVisible: (state, action) => {
            state.chatVisible = action.payload;
        },
        setChatTarget: (state, action) => {
            state.chatTarget = action.payload;
        },
    },
});

export const { setChatVisible, setChatTarget } = chatSlice.actions;

export default chatSlice.reducer;