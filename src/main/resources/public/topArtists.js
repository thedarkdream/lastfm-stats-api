export default {
    props: {
        artists: Object
    },
    template: `
        <table>
            <tr v-for="artist in artists">
                <td>{{ artist.name }}</td>
                <td>{{ artist.listens }}</td>
            </tr>
        </table>
    `,
     mounted: function() {

     }
}