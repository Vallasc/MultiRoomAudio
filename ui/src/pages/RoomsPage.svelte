<script>
    import { Page, Navbar, Fab, Icon, Link, ListItem, List, } from 'framework7-svelte'
</script>

<Page>
    <!-- Top Navbar -->
    <Navbar title="House rooms" backLink="Back"></Navbar>
    <Fab position="right-bottom" href="/calibration/">
        <Icon md="material:add"></Icon>
    </Fab>

    <List>
        <ListItem title="Cucina">
            <span slot="after">
                <Link  iconMd="material:delete" color="red"></Link>
            </span>
        </ListItem>
        <ListItem title="Camera Luca">
            <span slot="after">
                <Link  iconMd="material:delete" color="red"></Link>
            </span>
        </ListItem>        
        <ListItem title="Cammera Andrea">
            <span slot="after">
                <Link  iconMd="material:delete" color="red"></Link>
            </span>
        </ListItem>
    </List>
</Page>

<style>
</style>