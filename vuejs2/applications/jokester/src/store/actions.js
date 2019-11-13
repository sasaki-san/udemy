// https://official-joke-api.appspot.com/random_ten

import * as types from "./mutation-types"

export const initJokes = ({commit}) => {
  fetch('https://official-joke-api.appspot.com/random_ten', {
    method: "GET"
  })
  .then(r => r.json())
  .then(j => commit(types.INIT_JOKES, j))
}

export const addJoke = ({commit}) => {
  fetch('https://official-joke-api.appspot.com/random_joke', {
    method: "GET"
  })
  .then(r => r.json())
  .then(j => commit(types.ADD_JOKE, j))
}

export const removeJoke = ({commit}, index) => {
  commit(types.REMOVE_JOKE, index)
}
